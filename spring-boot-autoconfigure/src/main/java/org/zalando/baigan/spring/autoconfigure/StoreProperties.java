package org.zalando.baigan.spring.autoconfigure;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

@SuppressWarnings("WeakerAccess")
final class StoreProperties {

    enum StoreType {
        NAMESPACED(singletonList("stores"), singletonList("lazy")),
        CHAINED(singletonList("stores"), singletonList("lazy")),
        FILE(singletonList("location"), asList("lazy", "cache", "format")),
        ETCD(asList("location", "style"), asList("lazy", "cache", "format"));

        private final List<String> requiredFields;
        private final List<String> optionalFields;

        StoreType(final List<String> requiredFields) {
            this(requiredFields, singletonList("lazy"));
        }

        StoreType(final List<String> requiredFields, final List<String> optionalFields) {
            this.requiredFields = concat(of("type"), requiredFields.stream()).collect(toList());
            this.optionalFields = optionalFields;
        }

        List<String> getRequiredFields() {
            return requiredFields;
        }

        List<String> getOptionalFields() {
            return optionalFields;
        }
    }

    enum Format {
        JSON,
        YAML
    }

    enum EtcdStyle {
        CONFIGURATION_FILE,
        CONFIGURATION_KEY
    }

    StoreType type;
    Boolean lazy;
    Duration cache;
    String location;
    EtcdStyle style;
    Format format;
    Map<String, StoreProperties> stores;

    public StoreType getType() {
        return type;
    }

    public void setType(final StoreType type) {
        this.type = type;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public void setLazy(final Boolean lazy) {
        this.lazy = lazy;
    }

    public Duration getCache() {
        return cache;
    }

    public void setCache(final Duration cache) {
        this.cache = cache;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public EtcdStyle getStyle() {
        return style;
    }

    public void setStyle(final EtcdStyle style) {
        this.style = style;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(final Format format) {
        this.format = format;
    }

    public Map<String, StoreProperties> getStores() {
        return stores;
    }

    public void setStores(final Map<String, StoreProperties> stores) {
        this.stores = stores;
    }

}
