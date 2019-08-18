package sarf.automation.events.imap.listener;

import javax.mail.Folder;
import javax.mail.event.ConnectionEvent;
import javax.mail.event.ConnectionListener;
import sarf.automation.events.imap.impl.IMAPListenerBridge;

public class MyConnectionListener implements ConnectionListener {

  private final Folder folder;
  private final IMAPListenerBridge bridge;

  public MyConnectionListener(Folder folder, IMAPListenerBridge bridge) {
    this.folder = folder;
    this.bridge = bridge;
  }

  @Override
  public void opened(ConnectionEvent e) {
    bridge.updateFolderStatus(folder, FolderStatus.OPEN);
  }

  @Override
  public void disconnected(ConnectionEvent e) {

  }

  @Override
  public void closed(ConnectionEvent e) {
    bridge.updateFolderStatus(folder, FolderStatus.CLOSED);
  }
}
