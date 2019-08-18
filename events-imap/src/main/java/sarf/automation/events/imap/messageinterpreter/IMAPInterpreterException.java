package sarf.automation.events.imap.messageinterpreter;

import sarf.automation.events.imap.IMAPException;

public class IMAPInterpreterException extends IMAPException {

  public IMAPInterpreterException() {
    super();
  }

  public IMAPInterpreterException(String message) {
    super(message);
  }

  public IMAPInterpreterException(String message, Throwable cause) {
    super(message, cause);
  }

  public IMAPInterpreterException(Throwable cause) {
    super(cause);
  }

  public IMAPInterpreterException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
