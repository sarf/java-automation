package sarf.automation.events.imap.impl;

import static java.util.logging.Logger.getLogger;
import static sarf.automation.events.imap.config.requirements.CommonCapabilities.SSLTLS;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;
import lombok.Data;
import sarf.automation.events.imap.IMAPAuthenticationException;
import sarf.automation.events.imap.IMAPCloseException;
import sarf.automation.events.imap.IMAPEventSource;
import sarf.automation.events.imap.IMAPEventSourceListener;
import sarf.automation.events.imap.IMAPException;
import sarf.automation.events.imap.config.IMAPConfig;
import sarf.automation.events.imap.config.authentication.IMAPAuthentication.AuthenticationType;
import sarf.automation.events.imap.config.authentication.IMAPAuthenticationUsernamePassword;
import sarf.automation.events.imap.listener.FolderStatus;
import sarf.automation.events.imap.messageinterpreter.MessageInterpreterFactory;
import sarf.automation.events.mail.Mail;
import sarf.automation.events.mail.NewMailEvent;

@Data
class IMAPEventSourceImpl implements IMAPEventSource {

  private static final Logger log = getLogger(IMAPEventSourceImpl.class.getName());
  private static final ExecutorService background = Executors.newCachedThreadPool();

  private final Deque<QueueEntry> queueEntries = new LinkedList<>();
  private final Map<Folder, FolderStatus> folderStatus = new HashMap<>();
  private final IMAPListenerBridge bridge = new IMAPListenerBridge(this);


  private final IMAPConfig config;
  ExecutorService eventSender = Executors.newSingleThreadExecutor();
  Future<?> eventSenderSubmitted;
  private Store store;

  public IMAPEventSourceImpl(IMAPConfig config) {
    this.config = config;
    IMAPMailListenerFactory.addSource(this);
  }

  private static String hashWithSHA256(String source) {
    try {
      return new String(MessageDigest.getInstance("SHA-256").digest(source.getBytes()));
    } catch (NoSuchAlgorithmException e) {
      return source;
    }
  }

  @Override
  public void close() throws IMAPException {
    try {
      bridge.closing(this);
      store.close();
    } catch (MessagingException e) {
      throw new IMAPCloseException(e);
    }
  }

  @Override
  public void connect() throws IMAPException {
    config.validate();
    if (!AuthenticationType.USERNAME_PASSWORD.equals(config.getAuthentication().getType())
        || !(config.getAuthentication() instanceof IMAPAuthenticationUsernamePassword)) {
      throw new IllegalArgumentException("Does not know how to connect using " + config.getAuthentication());
    }
    Properties props = System.getProperties();
    String protocol = config.getCapabilities().contains(SSLTLS) ? "imaps" : "imap";
    props.setProperty("mail.store.protocol", protocol);
    try {
      Session session = Session.getDefaultInstance(props, null);
      Store connectStore = session.getStore(protocol);
      connectStore.addStoreListener(bridge.getStoreListener());
      IMAPAuthenticationUsernamePassword auth = (IMAPAuthenticationUsernamePassword) config.getAuthentication();
      connectStore.connect(config.getServerAddress(), config.getServerPort(), auth.getUsername(), auth.getPassword());
      this.store = connectStore;

      bridge.connected(this);

      config.getFolders().forEach(this::addFolderByName);

      background.submit(this::checkForAndIssueNewMails);
    } catch (AuthenticationFailedException e) {
      throw new IMAPAuthenticationException(e);
    } catch (MessagingException e) {
      throw new IMAPException(e);
    }
  }

  @Override
  public void addSourceListener(IMAPEventSourceListener sourceListener) {
    bridge.addSourceListener(sourceListener);
  }

  @Override
  public void removeSourceListener(IMAPEventSourceListener sourceListener) {
    bridge.removeSourceListener(sourceListener);
  }

  void updateFolderStatus(Folder folder, FolderStatus status) {
    folderStatus.put(folder, status);
  }

  private void checkForAndIssueNewMails() {
    FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.RECENT), false);

    for (String folderName : config.getFolders()) {
      try {
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        bridge.messagesAdded(folder, folder.search(ft));
      } catch (MessagingException e) {
        log.info(() -> String.format("Problem accessing folder %s", folderName));
      }
    }
  }

  private boolean addFolderByName(String f) {
    try {
      Folder folder = store.getFolder(f);
      folder.addMessageCountListener(bridge.addMessageCountListener(folder));
      folder.addConnectionListener(bridge.getConnectionListener(folder));
      updateFolderStatus(folder, FolderStatus.OPEN);
      return true;
    } catch (MessagingException e) {
      return false;
    }
  }

  void messagesAdded(Folder folder, Message[] messages) {
    if (messages != null && messages.length > 0) {
      queueEntries.add(new QueueEntry(folder, messages));
    }
  }

  void sendEvents() {
    if (eventSenderSubmitted != null && !eventSenderSubmitted.isDone()) {
      return;
    }
    if (queueEntries.peekFirst() != null) {
      QueueEntry entry = queueEntries.removeFirst();
      eventSenderSubmitted = eventSender.submit(() -> sendEventsFor(entry));
    }
  }

  private void sendEventsFor(QueueEntry entry) {
    NewMailEvent newMailEvent = new NewMailEvent(this, Arrays.stream(entry.getMessages())
                                                             .map(message -> fromMessageToMail(entry.getFolder(),
                                                                                               message))
                                                             .filter(Objects::nonNull)
                                                             .collect(Collectors.toList()));
    bridge.newMailEvent(newMailEvent);
  }

  private Mail fromMessageToMail(Folder folder, Message message) {
    try {
      String sourceName = hashWithSHA256(String.format("%s:%d", config.getServerAddress(), config.getServerPort()));

      String content = MessageInterpreterFactory.interpretMessageToString(message);

      return new Mail(sourceName, folder.getFullName(), message.getSubject(), content);
    } catch (MessagingException e) {
      log.info(() -> String.format("Failed to convert %s (from %s / %s) to mail", message, folder, folder.getStore()));
      return null;
    }
  }


}
