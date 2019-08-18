package sarf.automation.events.imap.impl;

import sarf.automation.events.imap.IMAPEventSource;
import sarf.automation.events.imap.config.IMAPConfig;

public interface IMAPEventSourceFactory {

  static IMAPEventSource from(IMAPConfig imapConfig) {
    return new IMAPEventSourceImpl(imapConfig);
  }

}
