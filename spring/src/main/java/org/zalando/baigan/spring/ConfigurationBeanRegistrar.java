package org.zalando.baigan.spring;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import java.util.Map;

import static java.util.Objects.requireNonNull;

final class ConfigurationBeanRegistrar implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware {

    private static final String ATTRIBUTE_ENABLED = "enabled";
    private static final String ATTRIBUTE_BASE_PACKAGE_CLASSES = "basePackageClasses";

    private ConfigurationBeanRegistrarSupport support;

    @Override
    public void setBeanClassLoader(final ClassLoader classLoader) {
        this.support = new ConfigurationBeanRegistrarSupport(classLoader);
    }

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        requireNonNull(support, "Expected class loader to be set");

        final Map<String, Object> attributeMap = metadata.getAnnotationAttributes(EnableBaigan.class.getName(), false);
        final AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributeMap);

        if (attributes == null) {
            return;
        }

        if (!attributes.getBoolean(ATTRIBUTE_ENABLED)) {
            return;
        }

        final Class<?>[] basePackageClasses = attributes.getClassArray(ATTRIBUTE_BASE_PACKAGE_CLASSES);
        if (basePackageClasses.length == 0) {
            support.registerConfigurationBeans(registry, metadata.getClassName());
        } else {
            support.registerConfigurationBeans(registry, basePackageClasses);
        }
    }
}
