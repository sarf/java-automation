package sarf.automation.events.imap.impl;

import javax.mail.Folder;
import javax.mail.Message;
import lombok.Data;

@Data
final class QueueEntry {
  private final Folder folder;
  private final Message[] messages;

}
