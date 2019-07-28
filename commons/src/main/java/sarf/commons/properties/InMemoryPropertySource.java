package sarf.commons.properties;

import static sarf.commons.properties.StandardPropertyFeature.TYPED_KEYS;
import static sarf.commons.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons.util.CollectionUtil.cross;
import static sarf.commons.util.OptionalUtil.toOpt;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.Data;
import lombok.NonNull;

@Data
class InMemoryPropertySource<K, V> implements PropertySource<K, V> {

  private final Map<K, V> map;

  public InMemoryPropertySource(@NonNull Map<K, V> map) {
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
