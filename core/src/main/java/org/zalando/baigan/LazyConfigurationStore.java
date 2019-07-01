package org.zalando.baigan;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LazyConfigurationStore implements ConfigurationStore {

    public static ConfigurationStore lazy(final Supplier<ConfigurationStore> supplier) {
        return new LazyConfigurationStore(supplier);
    }

    private final Supplier<ConfigurationStore> memoizingSupplier;

    private LazyConfigurationStore(final Supplier<ConfigurationStore> delegate) {
        this.memoizingSupplier = new MemoizingSupplier(delegate);
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        final ConfigurationStore store = memoizingSupplier.get();
        return store.getConfiguration(namespace, key);
    }

    static class MemoizingSupplier implements Supplier<ConfigurationStore> {

        private final Map<Object, ConfigurationStore> state = new ConcurrentHashMap<>(1);
        private final Supplier<ConfigurationStore> delegate;

        MemoizingSupplier(final Supplier<ConfigurationStore> delegate) {
            this.delegate = delegate;
        }

        @Override
        public ConfigurationStore get() {
            return state.computeIfAbsent(delegate, $ -> delegate.get());
        }
    }
}
