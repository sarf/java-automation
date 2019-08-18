package sarf.automation.events.imap.config.authentication;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class IMAPAuthenticationUsernamePassword implements IMAPAuthentication {

  private final String username;
  private final String password;

  @Override
  public AuthenticationType getType() {
    return AuthenticationType.USERNAME_PASSWORD;
  }
}

