package org.zalando.baigan.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.zalando.baigan.model.Configuration;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public abstract class AbstractConfigurationRepository implements ConfigurationRepository {

    ObjectMapper objectMapper = new ObjectMapper().registerModule(new GuavaModule());

    @Nonnull
    List<Configuration> getConfigurations(final String text) {
        try {
            return objectMapper.readValue(text, new TypeReference<List<Configuration>>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to deserialize the Configuration.", e);
        }
    }
}
