package sarf.automation.events.imap;

public class IMAPCloseException extends IMAPException {

  public IMAPCloseException() {
    super();
  }

  public IMAPCloseException(String message) {
    super(message);
  }

  public IMAPCloseException(String message, Throwable cause) {
    super(message, cause);
  }

  public IMAPCloseException(Throwable cause) {
    super(cause);
  }

  public IMAPCloseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
