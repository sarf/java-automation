package sarf.automation.events.imap;

public class IMAPAuthenticationException extends IMAPException {

  public IMAPAuthenticationException(String message) {
    super(message);
  }

  public IMAPAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

  public IMAPAuthenticationException(Throwable cause) {
    super(cause);
  }

  public IMAPAuthenticationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
