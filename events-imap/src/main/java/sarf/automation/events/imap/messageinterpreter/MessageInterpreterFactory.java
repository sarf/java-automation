package sarf.automation.events.imap.messageinterpreter;

import static java.util.logging.Logger.getLogger;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageInterpreterFactory {

  private final static Logger log = getLogger(MessageInterpreterFactory.class.getName());

  private static final Map<String, Class<? extends MessageInterpreter>> interpreters = new ConcurrentHashMap<>();

  public static Collection<MessageInterpreter> getMessageInterpretersFor(Message message) throws IMAPInterpreterException {
    try {
      MessageInterpreter byContentType = interpreters.get(message.getContentType()).getDeclaredConstructor()
                                                          .newInstance();
      return Collections.singleton(byContentType);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | MessagingException e) {
      throw new IMAPInterpreterException(e);
    }
  }

  static void addMessageInterpreterFor(String contentType, Class<? extends MessageInterpreter> messageInterpreter) {
    interpreters.put(contentType, messageInterpreter);
  }

  public static String interpretMessageToString(Message message) {
    try {
      for (MessageInterpreter interpreter : getMessageInterpretersFor(message)) {
        Optional<String> interpretation = attemptToInterpret(message, interpreter);
        if(interpretation.isPresent()) {
          return interpretation.get();
        }
      }
    } catch(IMAPInterpreterException ex) {
      log.fine(() -> String.format("Failed to get any interpreters for message %s", message));
    }
    return null;
  }

  private static Optional<String> attemptToInterpret(Message message, MessageInterpreter interpreter) {
    try {
      return Optional.ofNullable(interpreter.asText(message));
    } catch(IMAPInterpreterException e) {
      log.finer(() -> String.format("Failed to interpret message using %s", interpreter));
    }
    return Optional.empty();
  }
}
