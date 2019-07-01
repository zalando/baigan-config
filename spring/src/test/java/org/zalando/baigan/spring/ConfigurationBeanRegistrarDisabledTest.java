package org.zalando.baigan.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.zalando.baigan.ConfigurationStore;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringJUnitConfig
class ConfigurationBeanRegistrarDisabledTest {

    @BaiganConfiguration
    interface SomeConfiguration {

        String someString();
    }

    @Configuration
    @EnableBaigan(enabled = false)
    static class TestConfiguration {

        @Bean
        public ConfigurationStore myStore() {
            return new InMemoryConfigurationStore();
        }

    }

    @Autowired(required = false)
    private SomeConfiguration configuration;

    @Test
    void test() {
        assertNull(configuration);
    }

}
