package org.zalando.baigan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationTest {

    @Test
    void requiredValues() {
        assertThrows(NullPointerException.class, () -> new Configuration<>(null, "description", "value"));
        assertThrows(NullPointerException.class, () -> new Configuration<>("key", null, "value"));
        assertThrows(NullPointerException.class, () -> new Configuration<>("key", "description", null));
    }
}