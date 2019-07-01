package org.zalando.baigan.etcd;

import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.net.URI;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

final class EtcdConfigurationStore implements ConfigurationStore {

    private final Function<URI, Optional<String>> valueFetcher;
    private final BiFunction<String, String, Configuration> configurationMapper;
    private final URI baseUri;

    EtcdConfigurationStore(final Function<URI, Optional<String>> valueFetcher,
            final BiFunction<String, String, Configuration> configurationMapper,
            final URI baseUri) {
        this.valueFetcher = valueFetcher;
        this.configurationMapper = configurationMapper;
        this.baseUri = baseUri;
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        final URI fullUri = baseUri.resolve(namespace + "." + key);

        return valueFetcher.apply(fullUri)
                .map(node -> configurationMapper.apply(key, node));
    }

}
