package sarf.automation.events.imap.config;

import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import sarf.automation.events.imap.IMAPException;
import sarf.automation.events.imap.config.authentication.IMAPAuthentication;
import sarf.automation.events.imap.config.capabilities.IMAPServerCapability;
import sarf.automation.events.imap.config.capabilities.future.IgnorePortLimits;

@Builder
@Data
public class IMAPConfig {

  public static final int DEFAULT_PORT = 993;
  private final String serverAddress;

  @Default
  private final int serverPort = DEFAULT_PORT;
  private final Set<IMAPServerCapability> capabilities;
  private final IMAPAuthentication authentication;
  @Default
  private final Set<String> folders = new HashSet<>();


  public boolean hasCapability(Class<? extends IMAPServerCapability> capability) {
    return capabilities.stream()
                       .anyMatch(capability::isInstance);
  }

  public void validate() throws IMAPException {
    if(serverAddress == null || serverAddress.isEmpty()) {
      throw new IMAPException("The server address must be specified");
    }
    if(capabilities == null) {
      throw new IMAPException("The server capabilities must be a non-null Set, but may be empty.");
    }
    if(authentication == null) {
      throw new IMAPException("The server authentication must be non-null. If no authentication is used, use IMAPAuthenticationNone");
    }
    if (!hasCapability(IgnorePortLimits.class) && (serverPort <= 0 || serverPort > 65553)) {
      throw new IMAPException("The server port must be between 1 and 65535, or include the PASSTHROUGH_PORTS capability");
    }
    if(folders.isEmpty()) {
      throw new IMAPException("At least one folder must be specified; if nothing else, use 'Inbox'");
    }
  }

}
