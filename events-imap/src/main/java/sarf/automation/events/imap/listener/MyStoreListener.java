package sarf.automation.events.imap.listener;

import javax.mail.event.StoreEvent;
import javax.mail.event.StoreListener;
import sarf.automation.events.imap.impl.IMAPListenerBridge;

public class MyStoreListener implements StoreListener {

  private final IMAPListenerBridge bridge;

  public MyStoreListener(IMAPListenerBridge imapEventSource) {
    this.bridge = imapEventSource;
  }

  @Override
  public void notification(StoreEvent e) {
  }
}
