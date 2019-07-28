package sarf.commons.properties;

import static sarf.commons.properties.StandardPropertyFeature.TYPED_KEYS;
import static sarf.commons.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons.util.CollectionUtil.addAll;
import static sarf.commons.util.CollectionUtil.cross;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Data
class ChainedPropertySource<K, V> implements PropertySource<K, V> {

  @Getter(AccessLevel.PROTECTED)
  private final Collection<PropertySource<K, V>> propertySources;

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
    return cross(HashSet::new,
                 Stream.concat(Stream.<PropertyFeature>of(TYPED_KEYS, UNTYPED_KEYS),
                               Stream.concat(PropertySource.super.features().stream(), propertySources.stream().map(
                                   PropertySource::features).flatMap(Collection::stream))));
  }


}
