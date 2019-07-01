package org.zalando.baigan.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public final class ConfigurationBeanRegistrarSupport {

    private final AnnotatedInterfaceScanner scanner = new AnnotatedInterfaceScanner(BaiganConfiguration.class);
    private final ClassLoader classLoader;

    public ConfigurationBeanRegistrarSupport(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    void registerConfigurationBeans(final BeanDefinitionRegistry registry, final String baseClassName) {
        final Class<?> baseClass = loadClass(baseClassName);
        registerConfigurationBeans(registry, singletonList(baseClass.getPackage().getName()));
    }

    void registerConfigurationBeans(final BeanDefinitionRegistry registry, final Class<?>[] basePackageClasses) {
        final List<String> packages = Stream.of(basePackageClasses)
                .map(Class::getPackage)
                .map(Package::getName)
                .collect(toList());
        registerConfigurationBeans(registry, packages);
    }

    public void registerConfigurationBeans(final BeanDefinitionRegistry registry, final List<String> basePackages) {
        final Set<BeanDefinition> targets = basePackages.stream()
                .flatMap(pack -> scanner.findCandidateComponents(pack).stream())
                .collect(toSet());

        if (!targets.isEmpty()) {
            final AbstractBeanDefinition resolver = genericBeanDefinition(ConfigurationResolver.class)
                    .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                    .getBeanDefinition();
            registry.registerBeanDefinition(ConfigurationResolver.class.getName(), resolver);
        }

        targets.forEach(target -> {
            final Class<?> clazz = loadClass(target.getBeanClassName());
            final BeanDefinition definition = genericBeanDefinition(ConfigurationFactoryBean.class)
                    .addConstructorArgValue(clazz)
                    .getBeanDefinition();
            registry.registerBeanDefinition(clazz.getName(), definition);
        });
    }

    private Class<?> loadClass(final String configurationClass) {
        try {
            return classLoader.loadClass(configurationClass);
        } catch (final ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

}
