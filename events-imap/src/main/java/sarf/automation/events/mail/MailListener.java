package sarf.automation.events.mail;

import java.util.function.Predicate;

public interface MailListener {

  default Predicate<Mail> filter() {
    return a -> false;
  }

  void newMailEvent(MailEventNew mailEventNew);

}
