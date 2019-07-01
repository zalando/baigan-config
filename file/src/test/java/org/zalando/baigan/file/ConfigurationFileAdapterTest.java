package org.zalando.baigan.file;

import org.junit.jupiter.api.Test;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
class ConfigurationFileAdapterTest {

    private final Supplier<String> supplier = mock(Supplier.class);
    private final Function<String, ConfigurationFile> mapper = mock(Function.class);
    private final ConfigurationFileAdapter unit = new ConfigurationFileAdapter(supplier, mapper);

    @Test
    void mapsSuppliedContent() {
        final ConfigurationFile expected = new ConfigurationFile();
        final String content = "content";

        when(supplier.get()).thenReturn(content);
        when(mapper.apply(content)).thenReturn(expected);

        assertEquals(expected, unit.get());
    }
}