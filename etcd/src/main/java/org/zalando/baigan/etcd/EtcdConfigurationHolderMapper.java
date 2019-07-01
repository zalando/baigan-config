package org.zalando.baigan.etcd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.zalando.baigan.Configuration;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.BiFunction;

final class EtcdConfigurationHolderMapper implements BiFunction<String, String, Configuration> {

    private final ObjectMapper mapper;

    EtcdConfigurationHolderMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Configuration apply(final String key, final String value) {
        try {
            final ConfigurationHolder holder = mapper.readValue(value, ConfigurationHolder.class);
            return new Configuration<>(key, holder.getDescription(), holder.getValue());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
