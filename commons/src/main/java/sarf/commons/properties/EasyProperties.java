package sarf.commons.properties;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.Delegate;

@Data
public class EasyProperties<K, V> implements PropertySource<K, V> {

  @Delegate
  private final PropertySource<K, V> propertySource;

  public <U> Optional<U> getAs(K key, @NonNull Function<V, U> convert) {
    return get(key).map(convert);
  }

  public <U> U getAs(K key, @NonNull Function<V, U> convert, @NonNull Supplier<U> defaultValue) {
    return get(key).map(convert).orElseGet(defaultValue);
  }


}
