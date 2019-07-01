package org.zalando.baigan.file;

import org.junit.jupiter.api.Test;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.lang.ClassLoader.getSystemResource;

class UriConfigurationFileSupplierTest {

    private final UriConfigurationFileSupplier unit = unit();

    @Test
    void keepsNewlines() {
        final String content = unit.get();
        assertNotEquals(0, content.split("\n").length);
    }

    @Test
    void readsFile() {
        final String content = unit.get();
        assertTrue(content.contains("42"));
    }

    private UriConfigurationFileSupplier unit() {
        try {
            return new UriConfigurationFileSupplier(getSystemResource("example.json").toURI());
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}