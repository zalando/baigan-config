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

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("complex")
class BaiganAutoConfigurationComplexTest {

    @Configuration
    @EnableAutoConfiguration
    @ImportAutoConfiguration(BaiganAutoConfiguration.class)
    static class LocalConfiguration {

    }

    @Autowired
    private ApplicationContext context;

    @Test
    void buildsComplexStore() {
        final ConfigurationStore store = context.getBean(ConfigurationStore.class);
        assertNotNull(store);
    }
}
