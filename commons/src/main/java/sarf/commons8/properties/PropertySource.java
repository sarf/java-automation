package sarf.commons8.properties;

import lombok.NonNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface PropertySource<K, V> {

    @NonNull
    Optional<V> get(K key);

  default V get(K key, @NonNull Supplier<V> defaultValue) {
    return get(key).orElseGet(defaultValue);
  }

  default V get(K key, V defaultValue) {
    return get(key).orElse(defaultValue);
  }

    @NonNull
    default Set<K> keySet() {
        return Collections.emptySet();
    }

    @NonNull
    default Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
        return keySet().stream().map(converter).collect(Collectors.toSet());
    }

    @NonNull
    default Set<PropertyFeature> features() {
        return Collections.emptySet();
    }

}
