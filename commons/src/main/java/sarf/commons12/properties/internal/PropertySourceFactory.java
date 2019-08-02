package sarf.commons12.properties.internal;

import lombok.NonNull;
import sarf.commons12.properties.PropertySource;
import sarf.commons12.properties.StandardPropertyFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static sarf.commons12.util.FunctionUtil.FunctionAliases.whenNotNull;
import static sarf.commons12.util.FunctionUtil.nullSupplier;
import static sarf.commons12.util.StreamUtil.streamFrom;

@SuppressWarnings("WeakerAccess")
public class PropertySourceFactory {

    private PropertySourceFactory() {
        throw new UnsupportedOperationException();
    }

    public static <K, V> PropertySource<K, V> environment(@NonNull Function<K, String> keyConverter,
                                                          @NonNull Function<String, V> valueConverter) {
        return new EnvironmentPropertySource<>(keyConverter, valueConverter);
    }

    public static PropertySource<String, String> environment() {
        return new EnvironmentPropertySource<>(Function.identity(), Function.identity());
    }

    public static <K, V> PropertySource<K, V> systemProperties(@NonNull Function<K, String> keyConverter,
                                                               @NonNull Function<String, V> valueConverter) {
        return new SystemPropertySource<>(keyConverter, valueConverter);
    }

    public static PropertySource<String, String> systemProperties() {
        return new SystemPropertySource<>(Function.identity(), Function.identity());
    }

    public static <K, V> PropertySource<K, V> file(@NonNull Properties properties,
                                                   @NonNull Function<K, String> keyConverter,
                                                   @NonNull Function<String, V> valueConverter) {
        return new FilePropertySource<>(keyConverter, valueConverter, properties);
    }

    public static <K, V> PropertySource<K, V> inMemory(@NonNull Map<K, V> map) {
        return new InMemoryPropertySource<>(map);
    }

    @SafeVarargs
    public static <K, V> PropertySource<K, V> chain(@NonNull PropertySource<K, V>... sources) {
        return new ChainedPropertySource<>(streamFrom(sources).collect(Collectors.toCollection(HashSet::new)));
    }

    public static <K, V> PropertySource<K, V> chain(@NonNull Stream<PropertySource<K, V>> sources) {
        return new ChainedPropertySource<>(streamFrom(sources).collect(Collectors.toCollection(HashSet::new)));
    }

    /**
     * This is basically just a helper, that puts together systemProperties and environmentProperties with the default converters.
     *
     * @return a chained property source containing system properties and environment variables, in that order.
     */
    public static PropertySource<String, String> common() {
        return chain(systemProperties(), environment());
    }

    /**
     * Makes a property source immutable, in the sense that it will no longer reflect an outside state.
     * Can be useful to capture startup states and follow them until
     */
    public static <K, V> PropertySource<K, V> immutable(PropertySource<K, V> original, Function<Object, K> untypedConverter) {
        Map<K, V> map = new HashMap<>();
        if (original != null) {
            Consumer<K> putTypedKeys = k -> whenNotNull(original.get(k, nullSupplier()), v -> map.put(k, v));
            if (original.features().contains(StandardPropertyFeature.TYPED_KEYS)) {
                original.keySet().forEach(putTypedKeys);
            } else if (original.features().contains(StandardPropertyFeature.UNTYPED_KEYS)) {
                if (untypedConverter == null) {
                    throw new IllegalArgumentException("for untyped property sources an untyped key converter needs to be provided");
                }
                original.keySetFromUntyped(untypedConverter)
                        .forEach(putTypedKeys);
            }
        }
        return inMemory(map);
    }

    public static <K, V> ReplacablePropertySource<K, V> replacable(@NonNull PropertySource<K, V> start) {
        return new InternalReplacablePropertySource<>(start);
    }


    /**
     * A semi-mutable property source. It allows you to set a {@link PropertySource} that will replace the starting
     * property source. Setting the PropertySource to null will allow the starting PropertySource to be accessed through
     * the replacable property source again.
     */
    public interface ReplacablePropertySource<K, V> extends PropertySource<K, V> {
        @NonNull PropertySource<K, V> getPropertySource();

        void setPropertySource(PropertySource<K, V> propertySource);
    }

}
