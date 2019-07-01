package org.zalando.baigan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.time.Clock.systemUTC;
import static java.util.Objects.requireNonNull;

public final class CachedConfigurationStore implements ConfigurationStore {

    private static final Logger LOG = LoggerFactory.getLogger(CachedConfigurationStore.class);

    private final ConcurrentMap<NamespacedKey, CacheEntry> cache = new ConcurrentHashMap<>(20);
    private final ConfigurationStore delegate;
    private final Duration cacheEntryLifetime;
    private final Clock clock;

    public CachedConfigurationStore(final Duration cacheEntryLifetime, final ConfigurationStore delegate) {
        this(systemUTC(), cacheEntryLifetime, delegate);
    }

    CachedConfigurationStore(final Clock clock, final Duration cacheEntryLifetime, final ConfigurationStore delegate) {
        this.cacheEntryLifetime = cacheEntryLifetime;
        this.clock = clock;
        this.delegate = delegate;
    }

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        final CacheEntry entry = fetchFromCache(namespace, key);
        return Optional.ofNullable(entry).map(CacheEntry::getConfiguration);
    }

    private CacheEntry fetchFromCache(final String namespace, final String key) {
        final NamespacedKey cacheKey = new NamespacedKey(namespace, key);

        final Instant now = Instant.now(clock);
        final CacheEntry entry = computeIfAbsent(cacheKey, now);
        if (entry != null && isExpired(entry, now)) {
            LOG.debug("Cache entry [{}] is expired, re-computing.", entry);
            cache.remove(cacheKey, entry);
            return computeIfAbsent(cacheKey, now);
        }
        return entry;
    }

    private boolean isExpired(final CacheEntry entry, final Instant now) {
        final Duration between = Duration.between(entry.getCachedAt(), now).abs();
        return between.compareTo(cacheEntryLifetime) > 0;
    }

    private CacheEntry computeIfAbsent(final NamespacedKey key, final Instant now) {
        return cache.computeIfAbsent(key, $ ->
                delegate.getConfiguration(key.getNamespace(), key.getKey()).map(c ->
                        new CacheEntry(now, c)).orElse(null));
    }

    private static final class CacheEntry {
        private final Instant cachedAt;
        private final Configuration configuration;

        private CacheEntry(final Instant cachedAt, final Configuration configuration) {
            this.cachedAt = cachedAt;
            this.configuration = configuration;
        }

        Instant getCachedAt() {
            return cachedAt;
        }

        Configuration getConfiguration() {
            return configuration;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", CacheEntry.class.getSimpleName() + "[", "]")
                    .add("cachedAt=" + cachedAt)
                    .add("configuration=" + configuration)
                    .toString();
        }
    }

    private static final class NamespacedKey {
        private final String namespace;
        private final String key;

        private NamespacedKey(final String namespace, final String key) {
            this.namespace = requireNonNull(namespace);
            this.key = requireNonNull(key);
        }

        String getNamespace() {
            return namespace;
        }

        String getKey() {
            return key;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final NamespacedKey that = (NamespacedKey) o;
            return namespace.equals(that.namespace) &&
                    key.equals(that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(namespace, key);
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", NamespacedKey.class.getSimpleName() + "[", "]")
                    .add("namespace='" + namespace + "'")
                    .add("key='" + key + "'")
                    .toString();
        }
    }

}