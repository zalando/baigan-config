package org.zalando.baigan;

import java.util.Optional;
import java.util.function.BiFunction;

@FunctionalInterface
public interface ConfigurationStore extends BiFunction<String, String, Optional<Configuration>> {

    @Override
    default Optional<Configuration> apply(String namespace, String key) {
        return getConfiguration(namespace, key);
    }

    @SuppressWarnings("unchecked")
    default <T> Optional<Configuration<T>> getTypedConfiguration(final String namespace, final String key) {
        return getConfiguration(namespace, key).map(a -> (Configuration<T>) a);
    }

    Optional<Configuration> getConfiguration(final String namespace, final String key);
}
