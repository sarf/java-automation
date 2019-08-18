package sarf.automation.events.imap.config.authentication;

public interface IMAPAuthentication {

  enum AuthenticationType {
    NONE, USERNAME_PASSWORD;
  }

  AuthenticationType getType();

}
