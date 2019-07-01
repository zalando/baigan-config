package org.zalando.baigan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.time.Duration.ofMinutes;

class CachedConfigurationStoreTest {

    private final MockClock clock = new MockClock(Instant.now());
    private final ConfigurationStore delegate = mock(ConfigurationStore.class);
    private final CachedConfigurationStore store = new CachedConfigurationStore(clock, ofMinutes(1), delegate);

    @BeforeEach
    void setUp() {
        when(store.getConfiguration("ns", "key"))
                .thenReturn(Optional.of(new Configuration<>("ns.key", "description", "value")));
    }

    @Test
    void cachesConfiguration() {
        store.getConfiguration("ns", "key");
        store.getConfiguration("ns", "key");

        verify(delegate).getConfiguration("ns", "key");
        verifyNoMoreInteractions(delegate);
    }

    @Test
    void expiresCacheEntry() {
        store.getConfiguration("ns", "key");
        clock.forward(ofMinutes(2));
        store.getConfiguration("ns", "key");

        verify(delegate, times(2)).getConfiguration("ns", "key");
        verifyNoMoreInteractions(delegate);
    }

    @Test
    void cachesAfterExpiringCacheEntry() {
        store.getConfiguration("ns", "key");
        clock.forward(ofMinutes(2));
        store.getConfiguration("ns", "key");
        store.getConfiguration("ns", "key");

        verify(delegate, times(2)).getConfiguration("ns", "key");
        verifyNoMoreInteractions(delegate);
    }

    @Test
    void doesNotCacheMisses() {
        assertEquals(Optional.empty(), store.getConfiguration("ns", "missing"));
        assertEquals(Optional.empty(), store.getConfiguration("ns", "missing"));

        verify(delegate, times(2)).getConfiguration("ns", "missing");
        verifyNoMoreInteractions(delegate);
    }

    private static class MockClock extends Clock {
        private Instant instant;

        MockClock(final Instant instant) {
            this.instant = instant;
        }

        void forward(final Duration duration) {
            this.instant = instant.plus(duration);
        }

        @Override
        public Instant instant() {
            return instant;
        }

        @Override
        public ZoneId getZone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Clock withZone(final ZoneId zone) {
            throw new UnsupportedOperationException();
        }
    }
}
