package sarf.commons12.properties.internal;

import lombok.Data;
import lombok.NonNull;
import sarf.commons12.properties.PropertyFeature;
import sarf.commons12.properties.PropertySource;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Allows for delegating to a particular property source. While it is essentially the same as a
 * {@link ChainedPropertySource} with just 1 property source, this version is both cheaper and faster, but also allows
 * for a few neat extensions.
 */

@Data
class DelegatingPropertySource<K, V> implements PropertySource<K, V> {

    @NonNull
    private final PropertySource<K, V> propertySource;

    DelegatingPropertySource(@NonNull PropertySource<K, V> propertySource) {
        this.propertySource = propertySource;
    }

    @NonNull PropertySource<K, V> getPropertySource() {
        return propertySource;
    }

    @Override
    public @NonNull Optional<V> get(K key) {
        return getPropertySource().get(key);
    }

    @Override
    public @NonNull Set<K> keySet() {
        return getPropertySource().keySet();
    }

    @Override
    public @NonNull Set<K> keySetFromUntyped(@NonNull Function<Object, K> converter) {
        return getPropertySource().keySetFromUntyped(converter);
    }

    @Override
    public @NonNull Set<PropertyFeature> features() {
        return getPropertySource().features();
    }
}
