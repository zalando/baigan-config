package org.zalando.baigan.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

final class PrimitiveBoxer {
    private final Map<Class<?>, Method> boxing;

    PrimitiveBoxer() {
        this.boxing = Stream.of(new Class<?>[][]{
                {boolean.class, Boolean.class},
                {byte.class, Byte.class},
                {char.class, Character.class},
                {double.class, Double.class},
                {float.class, Float.class},
                {int.class, Integer.class},
                {long.class, Long.class},
                {short.class, Short.class}
        }).map(pair -> {
            try {
                final Class<?> primitive = pair[0];
                final Class<?> boxed = pair[1];
                return boxed.getDeclaredMethod("valueOf", primitive);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Expected boxed type to declare factory method", e);
            }
        }).collect(toMap(m -> m.getParameterTypes()[0], m -> m));
    }

    Object box(final Class<?> primitiveType, final Object value) throws InvocationTargetException, IllegalAccessException {
        final Method factory = boxing.get(primitiveType);
        return factory.invoke(null, value);
    }
}
