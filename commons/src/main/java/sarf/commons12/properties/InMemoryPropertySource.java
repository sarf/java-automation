package sarf.commons12.properties;

import lombok.Data;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

import static sarf.commons12.properties.StandardPropertyFeature.TYPED_KEYS;
import static sarf.commons12.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons12.util.CollectionUtil.cross;
import static sarf.commons12.util.OptionalUtil.toOpt;

@Data
class InMemoryPropertySource<K, V> implements PropertySource<K, V> {

  private final Map<K, V> map;

  InMemoryPropertySource(@NonNull Map<K, V> map) {
    this.map = Map.copyOf(map);
  }

  @Override
  public Optional<V> get(K key) {
    return map.containsKey(key) ? toOpt(map.get(key)) : Optional.empty();
  }

  @Override
  public Set<K> keySet() {
    return Collections.unmodifiableSet(map.keySet());
  }

  @Override
  public Set<PropertyFeature> features() {
    return cross(HashSet::new,
                 Stream.concat(Stream.of(TYPED_KEYS, UNTYPED_KEYS),
                               PropertySource.super.features().stream()));
  }

}
