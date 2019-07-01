package org.zalando.baigan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

public final class ChainedConfigurationStore implements ConfigurationStore {

    public static ConfigurationStore chain(final ConfigurationStore store, final ConfigurationStore... stores) {
        return new ChainedConfigurationStore(concat(of(store), of(stores)).collect(toList()));
    }

    public static ConfigurationStore chain(final List<ConfigurationStore> stores) {
        if (stores.isEmpty()) {
            throw new IllegalArgumentException("At least one store must be given");
        }
        return new ChainedConfigurationStore(stores);
    }

    private static final Logger LOG = LoggerFactory.getLogger(ChainedConfigurationStore.class);

    private final List<ConfigurationStore> stores;

    private ChainedConfigurationStore(List<ConfigurationStore> stores) {
        this.stores = stores;
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        for (ConfigurationStore store : stores) {
            final Optional<Configuration> configuration = getConfiguration(store, namespace, key);
            if (configuration.isPresent()) {
                return configuration;
            }
        }
        return Optional.empty();
    }

    private Optional<Configuration> getConfiguration(final ConfigurationStore store, final String namespace, final String key) {
        try {
            return store.getConfiguration(namespace, key);
        } catch (final RuntimeException e) {
            LOG.error("Exception on fetching configuration [{}.{}], skipping store [{}]", namespace, key, store, e);
            return Optional.empty();
        }
    }
}
