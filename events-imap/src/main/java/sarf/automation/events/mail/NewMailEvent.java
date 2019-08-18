package sarf.automation.events.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;
import sarf.automation.events.imap.IMAPEventSource;

@Data
public class NewMailEvent {

  private final IMAPEventSource source;
  private final Collection<Mail> mails;

  public NewMailEvent(IMAPEventSource source, Collection<Mail> mails) {
    this.source = source;
    this.mails = Collections.unmodifiableList(new ArrayList<>(mails));
  }

}
