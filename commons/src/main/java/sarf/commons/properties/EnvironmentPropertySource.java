package sarf.commons.properties;

import static sarf.commons.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons.util.CollectionUtil.cross;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NonNull;

@Data
class EnvironmentPropertySource<K, V> extends StringBasedPropertySource<K, V> {

  public EnvironmentPropertySource(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    super(keyConverter, valueConverter);
  }

  public EnvironmentPropertySource(@NonNull Function<String, V> valueConverter) {
    super(valueConverter);
  }

  @Override
  protected String getString(String key) {
    return System.getenv(key);
  }

  @Override
  public Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    return System.getenv().keySet().stream().map(converter).collect(Collectors.toSet());
  }

  @Override
  public Set<PropertyFeature> features() {
    return cross(HashSet::new,
                 Stream.concat(Stream.of(UNTYPED_KEYS),
                               super.features().stream()));
  }


}
