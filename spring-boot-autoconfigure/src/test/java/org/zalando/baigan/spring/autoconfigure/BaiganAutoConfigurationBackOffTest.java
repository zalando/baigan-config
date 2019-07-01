package org.zalando.baigan.spring.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.baigan.ConfigurationStore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("complex")
class BaiganAutoConfigurationBackOffTest {

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration(BaiganAutoConfiguration.class)
    static class LocalConfiguration {

        @Bean
        ConfigurationStore configurationStore() {
            return mock(ConfigurationStore.class);
        }

    }

    @Autowired
    private ApplicationContext context;

    @Test
    void backOffsOnStoreInContext() {
        final ConfigurationStore store = context.getBean(ConfigurationStore.class);
        assertEquals(context.getBean(LocalConfiguration.class).configurationStore(), store);
    }
}
