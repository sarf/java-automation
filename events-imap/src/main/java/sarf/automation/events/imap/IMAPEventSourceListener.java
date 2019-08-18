package sarf.automation.events.imap;

import sarf.automation.events.mail.NewMailEvent;

public interface IMAPEventSourceListener {

  void connected(IMAPEventSource source);

  void closing(IMAPEventSource source);

  void newMailEvent(NewMailEvent newMailEvent);

}
