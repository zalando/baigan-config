package org.zalando.baigan.spring.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.zalando.baigan.ConfigurationStore;
import org.zalando.baigan.spring.BaiganConfiguration;
import org.zalando.baigan.spring.autoconfigure.BaiganAutoConfigurationSimpleTest.LocalConfiguration.ExampleConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("simple")
class BaiganAutoConfigurationSimpleTest {

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration(BaiganAutoConfiguration.class)
    static class LocalConfiguration {

        @BaiganConfiguration
        interface ExampleConfiguration {
            String someText();
        }

    }

    @Autowired
    private ApplicationContext context;

    @Test
    void buildsSimpleStore() {
        assertNotNull(context.getBean(ConfigurationStore.class));
    }

    @Test
    void providesConfigurationBean() {
        final ExampleConfiguration example = context.getBean(ExampleConfiguration.class);
        assertNotNull(example);
        assertEquals("hello world", example.someText());
    }
}
