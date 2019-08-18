package sarf.automation.events.imap.messageinterpreter;

import javax.mail.Message;

public interface MessageInterpreter {

  String contentType();

  String asText(Message message) throws IMAPInterpreterException;

}
