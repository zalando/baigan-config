package org.zalando.baigan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static java.util.Collections.emptyMap;
import static java.util.Map.of;

class NamespacedConfigurationStoreTest {

    private final ConfigurationStore store1 = mock(ConfigurationStore.class);
    private final ConfigurationStore store2 = mock(ConfigurationStore.class);

    private final ConfigurationStore unit = NamespacedConfigurationStore.forward(of(
            "ns1", store1,
            "ns2", store2));

    @Test
    void delegatesByNamespace() {
        unit.getConfiguration("ns1", "key");
        unit.getConfiguration("ns2", "key");
        verify(store1).getConfiguration("ns1", "key");
        verify(store2).getConfiguration("ns2", "key");
        verifyNoMoreInteractions(store1, store2);
    }

    @Test
    void givesUp() {
        assertThrows(IllegalStateException.class, () -> unit.getConfiguration("ns3", "key"));
    }

    @Test
    void rejectsEmptyMapOfStores() {
        assertThrows(IllegalArgumentException.class, () -> NamespacedConfigurationStore.forward(emptyMap()));
    }
}