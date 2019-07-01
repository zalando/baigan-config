package org.zalando.baigan.file;

import java.util.function.Function;
import java.util.function.Supplier;

final class ConfigurationFileAdapter implements Supplier<ConfigurationFile> {

    private final Supplier<String> supplier;
    private final Function<String, ConfigurationFile> mapper;

    ConfigurationFileAdapter(final Supplier<String> supplier, final Function<String, ConfigurationFile> mapper) {
        this.supplier = supplier;
        this.mapper = mapper;
    }

    @Override
    public ConfigurationFile get() {
        final String content = supplier.get();
        return mapper.apply(content);
    }
}
