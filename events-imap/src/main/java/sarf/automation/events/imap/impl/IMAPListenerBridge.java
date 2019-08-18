package sarf.automation.events.imap.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.event.ConnectionListener;
import javax.mail.event.MessageCountListener;
import javax.mail.event.StoreListener;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import sarf.automation.events.imap.IMAPEventSource;
import sarf.automation.events.imap.IMAPEventSourceListener;
import sarf.automation.events.imap.config.IMAPConfig;
import sarf.automation.events.imap.listener.FolderStatus;
import sarf.automation.events.imap.listener.MyConnectionListener;
import sarf.automation.events.imap.listener.MyMessageCountListener;
import sarf.automation.events.imap.listener.MyStoreListener;
import sarf.automation.events.mail.NewMailEvent;

@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
@Data
public class IMAPListenerBridge implements IMAPEventSourceListener {

  private static final ExecutorService events = Executors.newSingleThreadExecutor();
  private final ListenerHandler<MessageCountListener> messageCountHandler = new ListenerHandler<>();
  private final ListenerHandler<ConnectionListener> connectionHandler = new ListenerHandler<>();
  private final ListenerHandler<IMAPEventSourceListener> sourceHandler = new ListenerHandler<>();
  private final Map<Folder, ConnectionListener> connectionListeners = new HashMap<>();

  private final IMAPEventSourceImpl source;

  private MyStoreListener storeListener;


  public MessageCountListener addMessageCountListener(Folder folder) {
    MyMessageCountListener listener = new MyMessageCountListener(folder, this);
    return messageCountHandler.addListener(listener);
  }

  public IMAPConfig getConfig() {
    return source.getConfig();
  }

  StoreListener getStoreListener() {
    if (storeListener == null) {
      storeListener = new MyStoreListener(this);
    }
    return storeListener;
  }

  ConnectionListener getConnectionListener(Folder folder) {
    MyConnectionListener myConnectionListener = new MyConnectionListener(folder, this);
    return connectionHandler.addListener(myConnectionListener);
  }

  public void updateFolderStatus(Folder folder, FolderStatus closed) {
    source.updateFolderStatus(folder, closed);
  }

  public void messagesAdded(Folder folder, Message[] messages) {
    if (messages != null && messages.length > 0) {
      source.messagesAdded(folder, messages);
    }
  }

  @Override
  public void connected(IMAPEventSource source) {
    Iterator<IMAPEventSourceListener> iterator = sourceHandler.iterateListeners();
    events.submit(() -> iterator.forEachRemaining(f -> f.connected(source)));
  }

  @Override
  public void closing(IMAPEventSource source) {
    Iterator<IMAPEventSourceListener> iterator = sourceHandler.iterateListeners();
    events.submit(() -> iterator.forEachRemaining(f -> f.closing(source)));
  }

  @Override
  public void newMailEvent(NewMailEvent newMailEvent) {
    sourceHandler.iterateListeners().forEachRemaining(f -> f.newMailEvent(newMailEvent));
  }

  void addSourceListener(IMAPEventSourceListener sourceListener) {
    sourceHandler.addListener(sourceListener);
  }

  void removeSourceListener(IMAPEventSourceListener sourceListener) {
    sourceHandler.removeListener(sourceListener);
  }
}
