package org.zalando.baigan.example.configuration;

import org.springframework.stereotype.Component;
import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.util.Optional;

@Component
public class DummyConfigurationStore implements ConfigurationStore {

    @Override
    public Optional<Configuration> getConfiguration(final String namespace, final String key) {
        return Optional.of(new Configuration<>(key, "", "Example-Service"));
    }

}
