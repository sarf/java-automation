package sarf.commons.properties;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NonNull;

@Data
class SystemPropertySource<K, V> extends StringBasedPropertySource<K, V> {

  public SystemPropertySource(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    super(keyConverter, valueConverter);
  }

  public SystemPropertySource(@NonNull Function<String, V> valueConverter) {
    super(valueConverter);
  }

  @Override
  protected String getString(String key) {
    return System.getProperty(key);
  }

  @Override
  public Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    return System.getProperties()
                 .keySet()
                 .stream()
                 .map(converter)
                 .collect(Collectors.toSet());
  }

  @Override
  public Set<PropertyFeature> features() {
    return Stream.concat(Stream.of(StandardPropertyFeature.UNTYPED_KEYS), super.features().stream())
                 .collect(Collectors.toSet());
  }
}
