package sarf.commons8.properties.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import sarf.commons8.properties.PropertyFeature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sarf.commons8.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons8.util.CollectionUtil.cross;

@EqualsAndHashCode(callSuper = true)
@Data
class EnvironmentPropertySource<K, V> extends StringBasedPropertySource<K, V> {

  EnvironmentPropertySource(@NonNull Function<K, String> keyConverter,
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
