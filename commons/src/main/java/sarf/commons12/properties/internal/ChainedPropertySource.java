package sarf.commons12.properties.internal;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import sarf.commons12.properties.PropertyFeature;
import sarf.commons12.properties.PropertySource;

import java.util.*;
import java.util.function.Function;

import static sarf.commons12.util.CollectionUtil.addAll;
import static sarf.commons12.util.CollectionUtil.union;

/**
 * Allows for chaining several property sources together.
 * <p>
 * Note that this checks the first chained property source first, then goes into the second and so on.
 * <p>
 * This can be somewhat counterintuitive for people who are used to having the most important being the last in the list.
 */
@Data
class ChainedPropertySource<K, V> implements PropertySource<K, V> {

    @Getter(AccessLevel.PROTECTED)
    private final Collection<PropertySource<K, V>> propertySources;

    ChainedPropertySource(@NonNull Collection<PropertySource<K, V>> propertySources) {
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

    /**
     * Returns all features that the chained property sources share.
     */
    @Override
    public Set<PropertyFeature> features() {
        return union(propertySources.stream().map(s -> features()));
    }


}
