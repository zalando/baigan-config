package org.zalando.baigan.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;

final class ConfigurationResolver {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationResolver.class);
    private static final PrimitiveBoxer PRIMITIVE_BOXER = new PrimitiveBoxer();
    private final ConfigurationStore store;

    public ConfigurationResolver(final ConfigurationStore store) {
        this.store = store;
    }

    Object resolve(final Class<?> target, final Method method) {
        final String namespace = target.getSimpleName();
        final String configurationName = method.getName();
        final Optional<Configuration> configuration = store.getConfiguration(namespace, configurationName);

        return configuration.map(Configuration::getValue)
                .flatMap(value -> ofNullable(resolveValue(value, method.getReturnType())))
                .orElseGet(() -> resolveDefault(method));
    }

    private Object resolveValue(final Object value, final Class<?> expectedType) {
        try {
            if (expectedType.isInstance(value)) {
                return value;
            } else if (expectedType.isEnum()) {
                return stream(expectedType.getEnumConstants())
                        .filter(enumValue -> enumValue.toString().equalsIgnoreCase(value.toString()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No valid enum value"));
            } else if (expectedType.isPrimitive()) {
                return resolvePrimitive(value, expectedType);
            } else {
                return resolveObject(value, expectedType);
            }
        } catch (final Exception e) {
            LOG.error("Unable to resolve [{}] of type [{}] to type [{}], falling back to default value", value, value.getClass(), expectedType, e);
            return null;
        }
    }

    private Object resolveObject(final Object value, final Class<?> expectedType) throws Exception {
        final Constructor<?> constructor = expectedType.getDeclaredConstructor(value.getClass());
        return constructor.newInstance(value);
    }

    private Object resolvePrimitive(final Object value, final Class<?> expectedType) throws Exception {
        // by (re-)boxing we fail fast (before unboxing happens on the interface)
        return PRIMITIVE_BOXER.box(expectedType, value);
    }

    private Object resolveDefault(final Method method) {
        // TODO: what about primitives? default values?
        return null;
    }

}
