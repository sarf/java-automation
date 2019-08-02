package sarf.commons8.properties;

import lombok.Data;
import lombok.NonNull;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * More flexible read-only version of Properties. Use {@link sarf.commons8.properties.internal.PropertySourceFactory} in
 * order to get hold of interesting {@link PropertySource}:s.
 * <p>
 * One of the major flaws of this class is that the null value is treated like Optional.empty() in a not
 * super-consistent manner.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
@Data
public class ReadOnlyProperties<K, V> implements PropertySource<K, V> {

    @NonNull
    private final PropertySource<K, V> propertySource;

    public ReadOnlyProperties(@NonNull PropertySource<K, V> propertySource) {
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
