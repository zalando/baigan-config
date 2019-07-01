package org.zalando.baigan.file;

import org.junit.jupiter.api.Test;
import org.zalando.baigan.Configuration;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class FileBasedConfigurationStoreTest {

    private final Supplier<ConfigurationFile> supplier = mock(Supplier.class);
    private final FileBasedConfigurationStore unit = new FileBasedConfigurationStore(supplier);

    @Test
    void emptyConfiguration() {
        when(supplier.get()).thenReturn(new ConfigurationFile());
        assertEquals(Optional.empty(), unit.getConfiguration("ns1", "key1"));
    }

    @Test
    void namespacedConfiguration() {
        final ConfigurationFile file = new ConfigurationFile();
        final ConfigurationFile.Namespace namespace = new ConfigurationFile.Namespace();
        namespace.getHolders().put("key1", new ConfigurationFile.ConfigurationHolder("description", "12"));
        file.getNamespaces().put("ns1", namespace);
        when(supplier.get()).thenReturn(file);

        final Configuration configuration = unit.getConfiguration("ns1", "key1")
                .orElseThrow(AssertionError::new);

        assertEquals("ns1.key1", configuration.getKey());
        assertEquals("12", configuration.getValue());
    }

    @Test
    void unknownKey() {
        final ConfigurationFile file = new ConfigurationFile();
        file.getNamespaces().put("ns1", new ConfigurationFile.Namespace());
        when(supplier.get()).thenReturn(file);
        assertEquals(Optional.empty(), unit.getConfiguration("ns1", "key1"));
    }

    @Test
    void unknownNamespace() {
        when(supplier.get()).thenReturn(new ConfigurationFile());
        assertEquals(Optional.empty(), unit.getConfiguration("ns1", "key1"));
    }
}