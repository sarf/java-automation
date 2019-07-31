package sarf.commons12.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sarf.commons12.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons12.util.CollectionUtil.cross;

@EqualsAndHashCode(callSuper = true)
@Data
class FilePropertySource<K, V> extends StringBasedPropertySource<K, V> {

  @NonNull
  private final Properties properties;

  FilePropertySource(@NonNull Function<K, String> keyConverter,
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
