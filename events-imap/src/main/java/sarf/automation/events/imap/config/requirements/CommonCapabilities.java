package sarf.automation.events.imap.config.requirements;

import lombok.Data;

@Data
public final class CommonCapabilities implements IMAPServerCapabilities {

  public static final CommonCapabilities SSLTLS = new CommonCapabilities();

  private CommonCapabilities() {
  }

}
