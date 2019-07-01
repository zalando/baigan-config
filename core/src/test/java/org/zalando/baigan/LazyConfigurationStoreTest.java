package org.zalando.baigan;

import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static java.util.stream.IntStream.range;

class LazyConfigurationStoreTest {

    private volatile AtomicInteger initializedCount = new AtomicInteger(0);
    private final ConfigurationStore delegate = mock(ConfigurationStore.class);

    @Test
    void delegates() {
        final ConfigurationStore unit = LazyConfigurationStore.lazy(this::initialize);
        unit.getConfiguration("ns1", "key");
        verify(delegate).getConfiguration("ns1", "key");
    }

    @Test
    void delegatesAgainOnException() {
        final AtomicInteger attempts = new AtomicInteger(0);
        final ConfigurationStore unit = LazyConfigurationStore.lazy(() -> {
            if (attempts.getAndIncrement() < 2) throw new RuntimeException("expected");
            return delegate;
        });

        assertThrows(RuntimeException.class, () -> unit.getConfiguration("ns1", "key"));
        assertThrows(RuntimeException.class, () -> unit.getConfiguration("ns1", "key"));
        unit.getConfiguration("ns1", "key");
        unit.getConfiguration("ns1", "key");
        verify(delegate, times(2)).getConfiguration("ns1", "key");
    }

    @Test
    void lazilyInitializedDelegate() {
        final ConfigurationStore unit = LazyConfigurationStore.lazy(this::initialize);
        assertEquals(0, initializedCount.get());
        unit.getConfiguration("ns1", "key");
        assertEquals(1, initializedCount.get());
    }

    @Test
    void initializedOnlyOnce() throws InterruptedException, ExecutionException {
        final ConfigurationStore unit = LazyConfigurationStore.lazy(this::initializeCostly);
        final ForkJoinPool pool = new ForkJoinPool(4);
        pool.submit(() -> range(0, 4).parallel().forEach($ -> unit.getConfiguration("ns1", "key"))).get();
        pool.shutdown();
        assertEquals(1, initializedCount.get());
    }

    private ConfigurationStore initialize() {
        initializedCount.addAndGet(1);
        return delegate;
    }

    private ConfigurationStore initializeCostly() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(50));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return initialize();
    }
}