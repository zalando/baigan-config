package org.zalando.baigan.spring.autoconfigure;

import org.zalando.baigan.ConfigurationStore;
import org.zalando.baigan.NamespacedConfigurationStore;
import org.zalando.baigan.etcd.EtcdStores;
import org.zalando.baigan.file.ConfigurationFile;
import org.zalando.baigan.file.FileStores;
import org.zalando.baigan.file.FileStores.StoreBuilder.SupplierBuilder;
import org.zalando.baigan.file.FileStores.StoreBuilder.SupplierBuilder.FormatBuilder;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.zalando.baigan.ChainedConfigurationStore.chain;
import static org.zalando.baigan.LazyConfigurationStore.lazy;
import static org.zalando.baigan.etcd.EtcdStores.etcd;
import static org.zalando.baigan.s3.S3Stores.s3;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.Format.JSON;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

final class StoreBuilder {

    private static final Duration DEFAULT_CACHE = Duration.ofMinutes(2);
    private static final StoreProperties.Format DEFAULT_FORMAT = JSON;

    ConfigurationStore buildStore(final StoreProperties store) {
        final StoreProperties.StoreType type = store.getType();
        switch (type) {
            case NAMESPACED:
                return buildLazy(store, () -> buildNamespaced(store));
            case CHAINED:
                return buildLazy(store, () -> buildChained(store));
            case FILE:
                return buildLazy(store, () -> buildFile(store));
            case ETCD:
                return buildLazy(store, () -> buildEtcd(store));
            default:
                throw new IllegalStateException("Unknown store type " + type);
        }
    }

    private ConfigurationStore buildLazy(final StoreProperties store, final Supplier<ConfigurationStore> builder) {
        return orDefault(store.getLazy(), false) ? lazy(builder) : builder.get();
    }

    private ConfigurationStore buildNamespaced(final StoreProperties store) {
        final Map<String, ConfigurationStore> stores = store.getStores().entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), buildStore(entry.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        return NamespacedConfigurationStore.forward(stores);
    }

    private ConfigurationStore buildChained(final StoreProperties store) {
        final List<ConfigurationStore> stores = store.getStores().values().stream()
                .map(this::buildStore)
                .collect(toList());
        return chain(stores);
    }

    private ConfigurationStore buildFile(final StoreProperties store) {
        return buildFile(store, false);
    }

    private ConfigurationStore buildFile(final StoreProperties store, final boolean servedViaEtcd) {
        final String location = store.getLocation();
        return buildFormat(store, buildFileSupplier(location, servedViaEtcd).apply(buildFileCached(store)));
    }

    private Function<SupplierBuilder, FormatBuilder> buildFileSupplier(final String location, final boolean servedViaEtcd) {
        final URI uri = URI.create(location);
        if (servedViaEtcd) {
            return builder -> builder.on(etcd(uri));
        } else if ("s3".equalsIgnoreCase(uri.getScheme())) {
            final String bucket = uri.getAuthority();
            final String key = uri.getPath().substring(1);
            return builder -> builder.on(s3(bucket, key));
        } else if (!uri.isAbsolute()) {
            final Path path = Paths.get(location);
            return builder -> builder.onLocalFile(path);
        } else {
            return builder -> builder.onUri(uri);
        }
    }

    private SupplierBuilder buildFileCached(final StoreProperties store) {
        return FileStores.builder().cached(orDefault(store.getCache(), DEFAULT_CACHE));
    }

    private ConfigurationStore buildEtcd(final StoreProperties store) {
        if (StoreProperties.EtcdStyle.CONFIGURATION_FILE == store.getStyle()) {
            return buildFile(store, true);
        } else {
            return buildFormat(store, new FormatBuilderAdapter(buildEtcdCache(store).on(URI.create(store.getLocation()))));
        }
    }

    private EtcdStores.StoreBuilder.UriBuilder buildEtcdCache(final StoreProperties store) {
        return EtcdStores.builder().cached(orDefault(store.getCache(), DEFAULT_CACHE));
    }

    private ConfigurationStore buildFormat(final StoreProperties store, final FormatBuilder builder) {
        switch (orDefault(store.getFormat(), DEFAULT_FORMAT)) {
            case JSON:
                return builder.asJson();
            case YAML:
                return builder.asYaml();
            default:
                throw new IllegalStateException("Unknown file format " + store.getFormat());
        }
    }

    private static <T> T orDefault(final T value, final T defaultValue) {
        return value == null ? defaultValue : value;
    }

    private static class FormatBuilderAdapter implements FormatBuilder {

        private final EtcdStores.StoreBuilder.UriBuilder.FormatBuilder builder;

        FormatBuilderAdapter(final EtcdStores.StoreBuilder.UriBuilder.FormatBuilder builder) {
            this.builder = builder;
        }

        @Override
        public ConfigurationStore asJson() {
            return builder.asJson();
        }

        @Override
        public ConfigurationStore asYaml() {
            return builder.asYaml();
        }

        @Override
        public ConfigurationStore asFormat(final Function<String, ConfigurationFile> mapper) {
            throw new UnsupportedOperationException();
        }
    }

}
