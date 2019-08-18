package sarf.automation.events.imap;

public interface IMAPEventSource extends AutoCloseable {

  @Override
  void close() throws IMAPException;

  void connect() throws IMAPException;

  sarf.automation.events.imap.config.IMAPConfig getConfig();

  void addSourceListener(IMAPEventSourceListener sourceListener);

  void removeSourceListener(IMAPEventSourceListener sourceListener);
}
