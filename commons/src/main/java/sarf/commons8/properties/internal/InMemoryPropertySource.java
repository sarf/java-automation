package sarf.commons8.properties.internal;

import lombok.Data;
import lombok.NonNull;
import sarf.commons8.properties.PropertyFeature;
import sarf.commons8.properties.PropertySource;

import java.util.*;
import java.util.stream.Stream;

import static sarf.commons8.properties.StandardPropertyFeature.TYPED_KEYS;
import static sarf.commons8.properties.StandardPropertyFeature.UNTYPED_KEYS;
import static sarf.commons8.util.CollectionUtil.cross;
import static sarf.commons8.util.OptionalUtil.toOpt;

@Data
class InMemoryPropertySource<K, V> implements PropertySource<K, V> {

  private final Map<K, V> map;

  InMemoryPropertySource(@NonNull Map<K, V> map) {
    this.map = Collections.unmodifiableMap(new HashMap<>(map));
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
