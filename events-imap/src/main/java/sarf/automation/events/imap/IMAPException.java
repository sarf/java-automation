package sarf.automation.events.imap;

public class IMAPException extends Exception {

  public IMAPException() {
  }

  public IMAPException(String message) {
    super(message);
  }

  public IMAPException(String message, Throwable cause) {
    super(message, cause);
  }

  public IMAPException(Throwable cause) {
    super(cause);
  }

  public IMAPException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
