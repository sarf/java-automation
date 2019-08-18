package sarf.automation.events.imap.listener;

import java.util.Set;
import javax.mail.Folder;
import javax.mail.event.FolderEvent;
import javax.mail.event.FolderListener;
import sarf.automation.events.imap.impl.IMAPListenerBridge;

public class MyFolderListener implements FolderListener {

  private final IMAPListenerBridge bridge;

  public MyFolderListener(IMAPListenerBridge bridge) {
    this.bridge = bridge;
  }

  @Override
  public void folderCreated(FolderEvent e) {
    Folder folder = e.getFolder() != null ? e.getFolder() : e.getNewFolder();
    if(folder != null) {
      Set<String> folders = bridge.getConfig().getFolders();
      if(folders.contains(folder.getName()) || folders.contains(folder.getFullName())) {
        folder.addMessageCountListener(bridge.addMessageCountListener(folder));

      }
    }
  }

  @Override
  public void folderDeleted(FolderEvent e) {

  }

  @Override
  public void folderRenamed(FolderEvent e) {

  }
}
