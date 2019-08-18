package sarf.automation.events.imap.messageinterpreter;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageInterpreterText extends MessageInterpreterBase {
  static {
    addToFactory(new MessageInterpreterText());
  }

  public MessageInterpreterText() {
    super("text/plain");
  }

  @Override
  protected String internalAsText(Message message) throws IMAPInterpreterException {
    try {
      String text = flavourExtractor(message.getDataHandler());
      if(text == null) {
        Object content = message.getContent();
        if(content instanceof String) {
          return content.toString();
        }
      }
      return null;
    } catch(MessagingException | IOException ex) {
      throw new IMAPInterpreterException(ex);
    }
  }

  private static String flavourExtractor(DataHandler dataHandler) throws IOException {
    for (DataFlavor flavour : dataHandler.getTransferDataFlavors()) {
      try (Reader reader = getReaderForText(flavour, dataHandler)) {
        if (reader != null) {
          StringWriter stringWriter = new StringWriter();
          reader.transferTo(stringWriter);
          return stringWriter.toString();
        }
      }
    }

    return null;
  }

  private static Reader getReaderForText(DataFlavor dataFlavor, DataHandler dataHandler) {
    try {
      return dataFlavor.getReaderForText(dataHandler);
    } catch(IOException | UnsupportedFlavorException ex) {
      return null;
    }
  }
}
