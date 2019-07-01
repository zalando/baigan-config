package org.zalando.baigan.file;

import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

final class FileBasedConfigurationStore implements ConfigurationStore {

    private final Supplier<ConfigurationFile> configurationSupplier;

    FileBasedConfigurationStore(final Supplier<ConfigurationFile> configurationSupplier) {
        this.configurationSupplier = configurationSupplier;
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        return ofNullable(configurationSupplier.get().getNamespaces().get(namespace))
                .flatMap(ns -> ofNullable(ns.getHolders().get(key)))
                .map(configuration -> buildConfiguration(namespace + "." + key, configuration));
    }

    private static Configuration buildConfiguration(final String key, final ConfigurationFile.ConfigurationHolder holder) {
        return new Configuration<>(key, holder.getDescription(), holder.getValue());
    }
}
