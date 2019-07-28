package sarf.commons.properties;

import static sarf.commons.util.StreamUtil.streamFrom;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;

public class PropertySourceFactory {

  private PropertySourceFactory() {
    throw new UnsupportedOperationException();
  }

  public static <K, V> PropertySource<K, V> environment(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    return new EnvironmentPropertySource<>(keyConverter, valueConverter);
  }

  public static <K, V> PropertySource<K, V> systemProperties(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    return new SystemPropertySource<>(keyConverter, valueConverter);
  }

  public static <K, V> PropertySource<K, V> file(@NonNull Properties properties,
      @NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    return new FilePropertySource<>(keyConverter, valueConverter, properties);
  }

  public static <K, V> PropertySource<K, V> inMemory(@NonNull Map<K, V> map) {
    return new InMemoryPropertySource<>(map);
  }

  @SafeVarargs
  public static <K, V> PropertySource<K, V> chain(@NonNull PropertySource<K, V>... sources) {
    return new ChainedPropertySource<>(streamFrom(sources).collect(Collectors.toCollection(HashSet::new)));
  }

}
