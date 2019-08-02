package sarf.commons12.properties.internal;

import lombok.NonNull;
import sarf.commons12.properties.PropertySource;

class InternalReplacablePropertySource<K, V> extends DelegatingPropertySource<K, V> implements PropertySourceFactory.ReplacablePropertySource<K, V> {

    private PropertySource<K, V> propertySource;

    InternalReplacablePropertySource(@NonNull PropertySource<K, V> propertySource) {
        super(propertySource);
    }

    @Override
    public @NonNull PropertySource<K, V> getPropertySource() {
        return propertySource == null ? super.getPropertySource() : propertySource;
    }

    @Override
    public void setPropertySource(PropertySource<K, V> propertySource) {
        this.propertySource = propertySource;
    }


}
