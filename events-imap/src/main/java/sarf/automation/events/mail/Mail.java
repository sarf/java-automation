package sarf.automation.events.mail;

import lombok.Data;

@Data
public class Mail {

  private final String sourceName;
  private final String sourceFolder;
  private final String subject;
  private final String textContent;

}
