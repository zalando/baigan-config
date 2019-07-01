package org.zalando.baigan.file;

import org.junit.jupiter.api.Test;
import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.lang.ClassLoader.getSystemResource;
import static java.util.Arrays.asList;

class FileStoresIntegrationTest {

    @Test
    void jsonStore() throws Exception {
        final ConfigurationStore store = FileStores.builder()
                .cached(Duration.ofHours(24))
                .onLocalFile(Paths.get(getSystemResource("example.json").toURI()))
                .asJson();
        assertExampleConfiguration(store);
    }

    @Test
    void yamlStore() throws Exception {
        final ConfigurationStore store = FileStores.builder()
                .cached(Duration.ofHours(24))
                .onLocalFile(Paths.get(getSystemResource("example.yaml").toURI()))
                .asYaml();
        assertExampleConfiguration(store);
    }

    private static void assertExampleConfiguration(final ConfigurationStore store) {
        assertConfiguration(store, "ns1", "key1", 42, "A description");
        assertConfiguration(store, "ns1", "key2", asList("1", "2", "3"), "Key description");
        assertConfiguration(store, "ns2", "key1", "Some value", "Some description");
        assertEquals(Optional.empty(), store.getConfiguration("ns3", "key1"));
    }

    private static void assertConfiguration(final ConfigurationStore store, final String namespace, final String key, final Object value,
            final String description) {
        final Configuration config = store.getConfiguration(namespace, key).orElseThrow(AssertionError::new);
        assertEquals(namespace + "." + key, config.getKey());
        assertEquals(value, config.getValue());
        assertEquals(description, config.getDescription());
    }

}