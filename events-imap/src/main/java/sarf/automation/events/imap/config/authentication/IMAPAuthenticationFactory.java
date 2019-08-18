package sarf.automation.events.imap.config.authentication;

public interface IMAPAuthenticationFactory {

  static IMAPAuthentication fromUsernameAndPassword(String username, String password) {
    return IMAPAuthenticationUsernamePassword.builder()
                                             .username(username)
                                             .password(password)
                                             .build();
  }

  static IMAPAuthenticationNone none() {
    return new IMAPAuthenticationNone();
  }

}
