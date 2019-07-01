package org.zalando.baigan.spring.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zalando.baigan.ConfigurationStore;

@Configuration
@Import(ConfigurationBeanAutoConfigureRegistrar.class)
@EnableConfigurationProperties(BaiganProperties.class)
public class BaiganAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = "baigan.store", name = "type")
    @ConditionalOnMissingBean(ConfigurationStore.class)
    protected static class ConfigurationStoreCreator {

        private final StoreBuilder storeBuilder = new StoreBuilder();

        @Bean
        public ConfigurationStore store(final BaiganProperties properties) {
            final StoreProperties store = properties.getStore();
            return storeBuilder.buildStore(store);
        }
    }

}
