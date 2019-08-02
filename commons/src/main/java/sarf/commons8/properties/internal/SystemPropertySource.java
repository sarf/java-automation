package sarf.commons8.properties.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import sarf.commons8.properties.PropertyFeature;
import sarf.commons8.properties.StandardPropertyFeature;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
class SystemPropertySource<K, V> extends StringBasedPropertySource<K, V> {

  SystemPropertySource(@NonNull Function<K, String> keyConverter,
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
