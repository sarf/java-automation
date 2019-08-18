package sarf.automation.events.mail;

public interface MailSource {

  void addMailListener(MailListener mailListener);

  void removeMailListener(MailListener mailListener);

}
