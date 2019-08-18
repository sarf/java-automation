package sarf.automation.events.imap.messageinterpreter;

import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.MessagingException;
import lombok.Data;

@Data
abstract class MessageInterpreterBase implements MessageInterpreter {

  private final String contentType;
  private final Pattern contentTypePattern;

  MessageInterpreterBase(String contentType) {
    this.contentType = contentType;
    contentTypePattern = Pattern.compile(contentType);
  }

  @Override
  public String contentType() {
    return getContentType();
  }

  @Override
  public String asText(Message message) throws IMAPInterpreterException {
    try {
      if (message == null) {
        throw new IllegalArgumentException("null is not a valid input parameter");
      }
      String messageType = message.getContentType();
      if (!contentTypePattern.matcher(messageType).matches()) {
        throw new IllegalArgumentException(String.format("message content type %s does not match %s",
                                                         messageType, this.contentType));
      }
      return internalAsText(message);
    } catch(MessagingException ex) {
      throw new IMAPInterpreterException(ex);
    }
  }

  protected abstract String internalAsText(Message message) throws IMAPInterpreterException;

  static void addToFactory(MessageInterpreter interpreter) {
    MessageInterpreterFactory.addMessageInterpreterFor(interpreter.contentType(), interpreter.getClass());
  }

}
