package sarf.automation.events.mail;

import sarf.automation.events.imap.impl.IMAPMailListenerFactory;

public interface MailListenerFactory {

  static void addMailListener(MailListener mailListener) {
    IMAPMailListenerFactory.addMailListener(mailListener);
  }

  static void removeMailListener(MailListener mailListener) {
    IMAPMailListenerFactory.removeMailListener(mailListener);
  }

}
