package org.zalando.baigan.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

final class CachingConfigurationFileSupplier implements Supplier<ConfigurationFile> {

    private static final Logger LOG = LoggerFactory.getLogger(CachingConfigurationFileSupplier.class);

    private final Supplier<ConfigurationFile> delegate;
    private final AtomicReference<ConfigurationFile> cached;

    CachingConfigurationFileSupplier(final Duration refreshInterval, final Supplier<ConfigurationFile> delegate) {
        this(new ScheduledThreadPoolExecutor(1), refreshInterval, delegate);
    }

    CachingConfigurationFileSupplier(final ScheduledExecutorService executor, final Duration refreshInterval, final Supplier<ConfigurationFile> delegate) {
        this.delegate = delegate;
        this.cached = new AtomicReference<>(delegate.get()); // hydrate cache early
        scheduleRefresh(executor, refreshInterval);
    }

    private void scheduleRefresh(final ScheduledExecutorService executor, final Duration refreshInterval) {
        final long refreshSeconds = refreshInterval.get(ChronoUnit.SECONDS);
        executor.scheduleAtFixedRate(this::refreshCache, refreshSeconds, refreshSeconds, TimeUnit.SECONDS);
    }

    private void refreshCache() {
        try {
            LOG.debug("Refreshing cache...");
            cached.set(delegate.get());
        } catch (final RuntimeException e) {
            LOG.error("Unable to refresh cache, keeping old value.", e);
        }
    }

    @Override
    public ConfigurationFile get() {
        return requireNonNull(cached.get());
    }

}
