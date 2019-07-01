package org.zalando.baigan.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConfigurationFileMapperTest {

    private final ObjectMapper mapper = mock(ObjectMapper.class);
    private final ConfigurationFileMapper unit = new ConfigurationFileMapper(mapper);

    @Test
    void mapsValueToConfiguration() throws IOException {
        final String content = "content";
        final ConfigurationFile expected = new ConfigurationFile();
        when(mapper.readValue(content, ConfigurationFile.class)).thenReturn(expected);
        assertEquals(expected, unit.apply(content));
    }

    @Test
    @SuppressWarnings("unchecked")
    void wrapsException() throws IOException {
        when(mapper.readValue(anyString(), any(Class.class))).thenThrow(new IOException("expected"));
        assertThrows(UncheckedIOException.class, () -> unit.apply("content"));
    }
}