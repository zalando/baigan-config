package org.zalando.baigan.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Objects.requireNonNull;

final class ConfigurationFactoryBean extends AbstractFactoryBean<Object> {

    private final Class<?> configurationInterface;

    public ConfigurationFactoryBean(final Class<?> configurationInterface) {
        this.configurationInterface = configurationInterface;
    }

    @Override
    public Class<?> getObjectType() {
        return configurationInterface;
    }

    @Override
    protected Object createInstance() {
        final BeanFactory factory = getBeanFactory();
        requireNonNull(factory, "Expected bean factory to be wired");

        final ConfigurationResolver resolver = factory.getBean(ConfigurationResolver.class);
        final ObjectInvocationHandler handler = new ObjectInvocationHandler((proxy, method, args) ->
                resolver.resolve(configurationInterface, method));
        return newProxyInstance(configurationInterface.getClassLoader(), new Class[]{configurationInterface}, handler);
    }

}
