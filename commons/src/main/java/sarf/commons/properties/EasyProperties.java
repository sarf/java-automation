package sarf.commons.properties;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Data;
import lombok.NonNull;

@Data
public class EasyProperties<K, V> implements PropertySource<K, V> {

  @NonNull
  private final PropertySource<K, V> propertySource;

  public EasyProperties(@NonNull PropertySource<K, V> propertySource) {
    this.propertySource = propertySource;
  }

  public <U> Optional<U> getAs(K key, @NonNull Function<V, U> convert) {
    return get(key).map(convert);
  }

  public <U> U getAs(K key, @NonNull Function<V, U> convert, @NonNull Supplier<U> defaultValue) {
    return get(key).map(convert).orElseGet(defaultValue);
  }

  @Override
  public Optional<V> get(K key) {
    return propertySource.get(key);
  }

  @Override
  public Set<K> keySet() {
    return propertySource.keySet();
  }

  @Override
  public Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    return propertySource.keySetFromUntyped(converter);
  }

  @Override
  public Set<PropertyFeature> features() {
    return propertySource.features();
  }
}
