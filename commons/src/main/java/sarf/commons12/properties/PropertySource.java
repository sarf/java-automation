package sarf.commons12.properties;

import lombok.NonNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface PropertySource<K, V> {

  Optional<V> get(K key);

  default V get(K key, @NonNull Supplier<V> defaultValue) {
    return get(key).orElseGet(defaultValue);
  }

  default V get(K key, V defaultValue) {
    return get(key).orElse(defaultValue);
  }

  default Set<K> keySet() {
    return Collections.emptySet();
  }

  default Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    return keySet().stream().map(converter).collect(Collectors.toSet());
  }

  default Set<PropertyFeature> features() {
    return Collections.emptySet();
  }

}
