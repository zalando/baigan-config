package org.zalando.baigan.file;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.Duration;
import java.util.HashSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CachingConfigurationFileSupplierTest {

    private final Duration refreshInterval = Duration.ofMinutes(7);
    private final ScheduledExecutorService executor = mock(ScheduledExecutorService.class);

    @Test
    void schedulesRefresh() {
        final Supplier<ConfigurationFile> supplier = () -> null;
        new CachingConfigurationFileSupplier(executor, refreshInterval, supplier);

        final ArgumentCaptor<Long> timeCaptor = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<TimeUnit> unitCaptor = ArgumentCaptor.forClass(TimeUnit.class);
        verify(executor).scheduleAtFixedRate(any(), timeCaptor.capture(), timeCaptor.capture(), unitCaptor.capture());

        assertEquals(1, new HashSet<>(timeCaptor.getAllValues()).size());
        final Duration actualInterval = Duration.of(timeCaptor.getValue(), unitCaptor.getValue().toChronoUnit());
        assertEquals(refreshInterval, actualInterval);
    }

    @Test
    void hydratesOnCreation() {
        final ConfigurationFile config = new ConfigurationFile();
        final CachingConfigurationFileSupplier unit = new CachingConfigurationFileSupplier(executor, refreshInterval, () -> config);
        assertEquals(config, unit.get());
    }

    @Test
    void cachesConfiguration() {
        final Supplier<ConfigurationFile> supplier = ConfigurationFile::new;
        final CachingConfigurationFileSupplier unit = new CachingConfigurationFileSupplier(executor, refreshInterval, supplier);
        assertEquals(unit.get(), unit.get());
    }

    @Test
    void refreshesConfiguration() {
        final Supplier<ConfigurationFile> supplier = ConfigurationFile::new;
        final CachingConfigurationFileSupplier unit = new CachingConfigurationFileSupplier(executor, refreshInterval, supplier);

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).scheduleAtFixedRate(captor.capture(), anyLong(), anyLong(), any());

        final ConfigurationFile pre = unit.get();
        captor.getValue().run();
        assertNotEquals(pre, unit.get());
    }

    @Test
    void failsIfHydrationFails() {
        final Supplier<ConfigurationFile> supplier = () -> {
            throw new RuntimeException("expected");
        };
        assertThrows(RuntimeException.class, () -> new CachingConfigurationFileSupplier(executor, refreshInterval, supplier));
    }

    @Test
    void ignoresFailureOnRefresh() {
        final AtomicBoolean firstTime = new AtomicBoolean(true);
        final Supplier<ConfigurationFile> supplier = () -> {
            if (firstTime.get()) {
                firstTime.set(false);
                return new ConfigurationFile();
            } else {
                throw new RuntimeException("expected");
            }
        };
        final CachingConfigurationFileSupplier unit = new CachingConfigurationFileSupplier(executor, refreshInterval, supplier);

        final ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).scheduleAtFixedRate(captor.capture(), anyLong(), anyLong(), any());

        final ConfigurationFile pre = unit.get();
        captor.getValue().run();
        assertEquals(pre, unit.get());
    }
}