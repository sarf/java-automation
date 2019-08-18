package sarf.automation.events.imap.config.authentication;

public class IMAPAuthenticationNone implements IMAPAuthentication {

  @Override
  public AuthenticationType getType() {
    return AuthenticationType.NONE;
  }
}
