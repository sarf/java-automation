package sarf.commons.properties;

import static sarf.commons.util.CollectionUtil.addAll;
import static sarf.commons.util.CollectionUtil.union;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
class ChainedPropertySource<K, V> implements PropertySource<K, V> {

  @Getter(AccessLevel.PROTECTED)
  private final Collection<PropertySource<K, V>> propertySources;

  public ChainedPropertySource(@NonNull Collection<PropertySource<K, V>> propertySources) {
    this.propertySources = Set.copyOf(propertySources);
  }

  @Override
  public Optional<V> get(K key) {
    return propertySources.stream()
                          .map(s -> get(key))
                          .reduce((a, b) -> a.or(() -> b))
                          .flatMap(s -> s);
  }

  @Override
  public Set<K> keySet() {
    Set<K> destSet = new HashSet<>();
    return propertySources.stream()
                          .map(PropertySource::keySet)
                          .reduce((a, b) -> addAll(addAll(destSet, a), b))
                          .orElse(Collections.emptySet());
  }

  @Override
  public Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
    Set<K> destSet = new HashSet<>();
    return propertySources.stream()
                          .map(s -> keySetFromUntyped(converter))
                          .reduce((a, b) -> addAll(addAll(destSet, a), b))
                          .orElse(Collections.emptySet());
  }

  @Override
  public Set<PropertyFeature> features() {
    return union(propertySources.stream().map(s -> features()));
  }


}
