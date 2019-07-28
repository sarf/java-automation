package sarf.commons.properties;

import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Data
@Getter(AccessLevel.PROTECTED)
public abstract class StringBasedPropertySource<K, V> implements PropertySource<K, V> {

  @NonNull
  private final Function<K, String> keyConverter;

  @NonNull
  private final Function<String, V> valueConverter;

  public StringBasedPropertySource(@NonNull Function<String, V> valueConverter) {
    this(k -> k == null ? null : k.toString(), valueConverter);
  }

  protected abstract String getString(String key);

  @Override
  public Optional<V> get(K key) {
    return Optional.ofNullable(valueConverter.apply(getString(keyConverter.apply(key))));
  }

}
