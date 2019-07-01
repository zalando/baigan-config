package org.zalando.baigan.spring;

import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

final class InMemoryConfigurationStore implements ConfigurationStore {

    private final Map<String, Configuration<?>> store;

    InMemoryConfigurationStore() {
        this.store = new HashMap<>();
    }

    <T> void addConfiguration(final String key, final T value) {
        store.put(key, new Configuration<>(key, "n/a", value));
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        return ofNullable(store.get(namespace + "." + key));
    }
}
