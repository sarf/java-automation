package sarf.automation.events.imap.listener;

import javax.mail.Folder;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import lombok.Data;
import sarf.automation.events.imap.impl.IMAPListenerBridge;

@Data
public class MyMessageCountListener implements MessageCountListener {

  private final Folder folder;
  private final IMAPListenerBridge bridge;

  public MyMessageCountListener(Folder folder, IMAPListenerBridge bridge) {
    this.folder = folder;
    this.bridge = bridge;
  }

  @Override
  public void messagesAdded(MessageCountEvent e) {
    bridge.messagesAdded(folder, e.getMessages());
  }

  @Override
  public void messagesRemoved(MessageCountEvent e) {
  }
}
