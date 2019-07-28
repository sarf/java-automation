package sarf.commons.properties;

import static sarf.commons.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons.util.CollectionUtil.cross;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NonNull;

@Data
class FilePropertySource<K, V> extends StringBasedPropertySource<K, V> {

  @NonNull
  private final Properties properties;

  public FilePropertySource(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter, @NonNull Properties properties) {
    super(keyConverter, valueConverter);
    this.properties = properties;
  }

  public FilePropertySource(@NonNull Function<String, V> valueConverter, @NonNull Properties properties) {
    super(valueConverter);
    this.properties = properties;
  }

  @Override
  protected String getString(String key) {
    return properties.getProperty(key);
  }

  @Override
  public Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    return properties.keySet()
                     .stream()
                     .map(converter)
                     .collect(Collectors.toSet());
  }

  @Override
  public Set<PropertyFeature> features() {
    return cross(HashSet::new,
                 Stream.concat(Stream.of(UNTYPED_KEYS),
                               super.features().stream()));
  }


}
