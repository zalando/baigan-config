package org.zalando.baigan.spring.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.zalando.baigan.ConfigurationStore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.autoconfigure.AutoConfigurations.of;

class BaiganAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(of(AutoConfigurationPackageConfiguration.class, BaiganAutoConfiguration.class));

    @Test
    void buildsLocalFileStore() {
        contextRunner.withPropertyValues(
                "baigan.store.type=file",
                "baigan.store.cache=2s",
                "baigan.store.location=src/test/resources/configuration.json",
                "baigan.store.format=json")
                .run(context -> assertThat(context).hasSingleBean(ConfigurationStore.class));
    }

    @Test
    void failsOnMissingRequiredParameter() {
        contextRunner.withPropertyValues(
                "baigan.store.type=file",
                "baigan.store.format=json")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void failsOnJunkParameter() {
        contextRunner.withPropertyValues(
                "baigan.store.type=file",
                "baigan.store.cache=2s",
                "baigan.store.location=src/test/resources/configuration.json",
                "baigan.store.style=configuration-file",
                "baigan.store.format=json")
                .run(context -> assertThat(context).hasFailed());
    }


    @Configuration
    @AutoConfigureBefore(BaiganAutoConfiguration.class)
    @Import(AutoConfigurationPackageConfiguration.AutoConfigurationPackageRegistrar.class)
    public static class AutoConfigurationPackageConfiguration {

        // making sure that there is a package registered for auto configuration
        public static class AutoConfigurationPackageRegistrar implements ImportBeanDefinitionRegistrar {

            @Override
            public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
                AutoConfigurationPackages.register(registry, BaiganAutoConfigurationTest.class.getPackageName());
            }
        }
    }
}