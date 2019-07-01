package org.zalando.baigan.spring.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.zalando.baigan.spring.ConfigurationBeanRegistrarSupport;
import java.util.List;

import static java.util.Objects.requireNonNull;

class ConfigurationBeanAutoConfigureRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, BeanClassLoaderAware {

    private ConfigurationBeanRegistrarSupport support;
    private BeanFactory beanFactory;

    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.support = new ConfigurationBeanRegistrarSupport(classLoader);
    }

    @Override
    public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
        requireNonNull(support, "Expected class loader to be set");
        requireNonNull(beanFactory, "Expected bean factory to be set");

        final List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
        support.registerConfigurationBeans(registry, basePackages);
    }

}
