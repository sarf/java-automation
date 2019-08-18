package sarf.automation.events.imap.impl;

import static org.junit.Assert.assertFalse;
import static sarf.automation.events.imap.impl.ExecutionHelper.waitUntil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.Ignore;
import org.junit.Test;
import sarf.automation.events.imap.IMAPEventSource;
import sarf.automation.events.imap.IMAPEventSourceListener;
import sarf.automation.events.imap.IMAPException;
import sarf.automation.events.imap.config.IMAPConfig;
import sarf.automation.events.imap.config.authentication.IMAPAuthentication;
import sarf.automation.events.imap.config.authentication.IMAPAuthenticationFactory;
import sarf.automation.events.imap.config.capabilities.IMAPServerCapability;
import sarf.automation.events.imap.config.capabilities.future.IgnorePortLimits;
import sarf.automation.events.imap.config.capabilities.security.SSLTLS;
import sarf.automation.events.mail.MailEventNew;

public class IMAPEventSourceImplTest {


  private static final Collection<Class<? extends IMAPServerCapability>> KNOWN_CAPABILITIES =
      java.util.List.of(SSLTLS.class, IgnorePortLimits.class);

  private static int parse(String str) {
    try {
      return Integer.parseInt(str);
    } catch (NumberFormatException e) {
      return IMAPConfig.DEFAULT_PORT;
    }
  }

  @Ignore
  @Test
  public void attemptToConnect() throws IOException, IMAPException {
    try (IMAPEventSource source = getSourceFromProperties(getProperties())) {

      TestSourceListener listener = new TestSourceListener();
      source.addSourceListener(listener);

      source.connect();

      waitUntil(() -> listener.connected.contains(source), 10, ChronoUnit.SECONDS);

      ExecutionHelper.sleeper(10 * 1000, 0).run();

      assertFalse(listener.mailEventNews.isEmpty());
    }
  }

  private Properties getProperties() throws IMAPException, IOException {
    Properties properties = new Properties();
    String filename = "resources/secret/test.login.properties";
    InputStream resourceAsStream = getClass().getClassLoader()
                                             .getResourceAsStream(filename);
    if (resourceAsStream == null) {
      throw new IMAPException(String.format("Could not load %s", filename));
    }
    properties.load(resourceAsStream);
    return properties;
  }

  private IMAPEventSource getSourceFromProperties(Properties properties) {
    Function<String, String> p = properties::getProperty;

    return create(IMAPConfig.builder()
                            .serverAddress(p.apply("serverAddress"))
                            .serverPort(getPort(p.apply("serverPort")))
                            .capabilities(getCapabilities(p))
                            .authentication(getAuthentication(p))
                            .folders(getFolders(p.apply("folders")))
                            .build());
  }

  private Set<String> getFolders(String folders) {
    return Set.of(folders.split(","));
  }

  private int getPort(String serverPort) {
    return parse(serverPort);
  }

  private IMAPEventSource create(IMAPConfig build) {
    return IMAPEventSourceFactory.from(build);
  }

  private IMAPAuthentication getAuthentication(Function<String, String> p) {
    String username = p.apply("username");
    String password = p.apply("password");
    if (username != null && password != null) {
      return IMAPAuthenticationFactory.fromUsernameAndPassword(username, password);
    }
    return IMAPAuthenticationFactory.none();
  }

  private Set<IMAPServerCapability> getCapabilities(Function<String, String> p) {
    Set<IMAPServerCapability> capabilities = new HashSet<>();

    String capabilitiesStr = p.apply("capabilities");
    for (String cap : capabilitiesStr.split(",")) {
      IMAPServerCapability capability = getAsCapability(cap);
      if (capability != null) {
        capabilities.add(capability);
      }
    }

    String insecure = p.apply("insecure");
    if (insecure == null || insecure.equalsIgnoreCase("no")) {
      capabilities.add(new SSLTLS());
    }

    return Collections.unmodifiableSet(capabilities);
  }

  private IMAPServerCapability getAsCapability(String str) {
    String upper = str.toUpperCase();
    Predicate<Class<? extends IMAPServerCapability>> prediate =
        c -> c.getName().equalsIgnoreCase(upper);
    prediate = prediate.or(c -> c.getName().toUpperCase().endsWith(upper));
    return KNOWN_CAPABILITIES.stream()
                             .filter(prediate)
                             .findAny()
                             .map(this::instantiate)
                             .orElse(null);
  }

  private IMAPServerCapability instantiate(Class<? extends IMAPServerCapability> c) {
    try {
      return c.getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      return null;
    }
  }

  private static class TestSourceListener implements IMAPEventSourceListener {

    private final Collection<MailEventNew> mailEventNews = new ConcurrentLinkedQueue<>();

    private final Set<IMAPEventSource> connected = new HashSet<>();
    private final Set<IMAPEventSource> closing = new HashSet<>();

    @Override
    public void connected(IMAPEventSource source) {
      connected.add(source);
    }

    @Override
    public void closing(IMAPEventSource source) {
      closing.add(source);
    }

    @Override
    public void newMailEvent(MailEventNew mailEventNew) {
      mailEventNews.add(mailEventNew);
    }
  }

}