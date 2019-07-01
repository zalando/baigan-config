package org.zalando.baigan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;

class ChainedConfigurationStoreTest {

    private final ConfigurationStore store1 = mock(ConfigurationStore.class);
    private final ConfigurationStore store2 = mock(ConfigurationStore.class);
    private final ConfigurationStore store3 = mock(ConfigurationStore.class);

    private final ConfigurationStore unit = ChainedConfigurationStore.chain(store1, store2, store3);

    @BeforeEach
    void setUp() {
        when(store1.getConfiguration(any(), any())).thenReturn(empty());
        when(store1.getConfiguration(any(), eq("1"))).then(ChainedConfigurationStoreTest::answer);
        when(store2.getConfiguration(any(), any())).thenReturn(empty());
        when(store2.getConfiguration(any(), eq("2"))).then(ChainedConfigurationStoreTest::answer);
        when(store3.getConfiguration(any(), any())).thenReturn(empty());
        when(store3.getConfiguration(any(), eq("3"))).then(ChainedConfigurationStoreTest::answer);
    }

    @Test
    void shortCircuits() {
        unit.getConfiguration("", "1");
        verify(store1).getConfiguration("", "1");
        verifyNoMoreInteractions(store2, store3);
    }

    @Test
    void delegatesUntilFound() {
        final Optional<Configuration> config = unit.getConfiguration("", "3");
        assertEquals("3", config.orElseThrow(AssertionError::new).getValue());
        verify(store1).getConfiguration("", "3");
        verify(store2).getConfiguration("", "3");
        verify(store3).getConfiguration("", "3");
    }

    @Test
    void delegatesOnErrorsUntilFound() {
        when(store1.getConfiguration(any(), any())).thenThrow(new RuntimeException("expected"));
        final Optional<Configuration> config = unit.getConfiguration("", "2");
        assertEquals("2", config.orElseThrow(AssertionError::new).getValue());
        verifyNoMoreInteractions(store3);
    }

    @Test
    void givesUp() {
        final Optional<Configuration> config = unit.getConfiguration("", "4");
        assertEquals(empty(), config);
    }

    @Test
    void rejectsEmptyListOfStores() {
        assertThrows(IllegalArgumentException.class, () -> ChainedConfigurationStore.chain(emptyList()));
    }

    @SuppressWarnings("unchecked")
    private static Optional<Configuration> answer(InvocationOnMock invocation) {
        final String key = invocation.getArgument(1);
        final Configuration config = new Configuration(key, key, key);
        return of(config);
    }
}