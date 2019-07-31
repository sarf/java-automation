package sarf.commons12.properties;

import java.util.Optional;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@Getter(AccessLevel.PROTECTED)
public abstract class StringBasedPropertySource<K, V> implements PropertySource<K, V> {

  @NonNull
  private final Function<K, String> keyConverter;

  @NonNull
  private final Function<String, V> valueConverter;

  StringBasedPropertySource(@NonNull Function<String, V> valueConverter) {
    this(k -> k == null ? null : k.toString(), valueConverter);
  }

  StringBasedPropertySource(@NonNull Function<K, String> keyConverter,
      @NonNull Function<String, V> valueConverter) {
    this.keyConverter = keyConverter;
    this.valueConverter = valueConverter;
  }

  protected abstract String getString(String key);

  @Override
  public Optional<V> get(K key) {
    return Optional.ofNullable(valueConverter.apply(getString(keyConverter.apply(key))));
  }

}
