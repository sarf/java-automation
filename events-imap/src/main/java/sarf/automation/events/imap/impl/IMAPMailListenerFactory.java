package sarf.automation.events.imap.impl;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import sarf.automation.events.imap.IMAPEventSource;
import sarf.automation.events.imap.IMAPEventSourceListener;
import sarf.automation.events.mail.Mail;
import sarf.automation.events.mail.MailEventNew;
import sarf.automation.events.mail.MailListener;

public class IMAPMailListenerFactory {

  private static final ExecutorService mailDispenser = Executors.newFixedThreadPool(2);
  private static final Collection<IMAPEventSource> sources = new ConcurrentLinkedDeque<>();
  private static final ListenerHandler<MailListener> mailListenerHandler = new ListenerHandler<>();

  private IMAPMailListenerFactory() {
    throw new UnsupportedOperationException();
  }

  public static boolean safeToTerminate() {
    return sources.isEmpty();
  }

  public static void addMailListener(MailListener mailListener) {
    mailListenerHandler.addListener(mailListener);
  }

  public static void removeMailListener(MailListener mailListener) {
    mailListenerHandler.removeListener(mailListener);
  }

  static void addSource(IMAPEventSourceImpl source) {
    sources.add(source);
    source.getBridge().addSourceListener(new SourceListener());
  }

  private static class SourceListener implements IMAPEventSourceListener {

    @Override
    public void connected(IMAPEventSource source) {
    }

    @Override
    public void closing(IMAPEventSource source) {
      sources.remove(source);
    }

    @Override
    public void newMailEvent(MailEventNew mailEventNew) {
      mailListenerHandler.iterateListeners()
                         .forEachRemaining(ml -> mailDispenser.submit(() -> filterAndCall(ml, mailEventNew)));
    }

    private void filterAndCall(MailListener ml, MailEventNew mailEventNew) {
      List<Mail> filtered = mailEventNew.getMails().stream()
                                        .filter(ml.filter())
                                        .collect(Collectors.toList());
      ml.newMailEvent(new MailEventNew(mailEventNew.getSource(), filtered));
    }
  }

}
