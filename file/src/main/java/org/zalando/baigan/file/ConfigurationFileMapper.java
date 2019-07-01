package org.zalando.baigan.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Function;

final class ConfigurationFileMapper implements Function<String, ConfigurationFile> {

    private final ObjectMapper mapper;

    ConfigurationFileMapper(final ObjectMapper objectMapper) {
        this.mapper = objectMapper;
    }

    @Override
    public ConfigurationFile apply(final String content) {
        try {
            return mapper.readValue(content, ConfigurationFile.class);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
