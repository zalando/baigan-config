package org.zalando.baigan.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

final class ObjectInvocationHandler implements InvocationHandler {

    private final InvocationHandler delegate;

    ObjectInvocationHandler(final InvocationHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();

        if (Object.class == declaringClass) {
            if (args == null && "hashCode".equals(method.getName())) {
                return super.hashCode();
            }
            if (args != null && args.length == 1 && "equals".equals(method.getName())
                    && method.getParameterTypes()[0] == Object.class) {
                return proxy == args[0];
            }
            if (args == null && "toString".equals(method.getName())) {
                return super.toString();
            }
        }
        return delegate.invoke(proxy, method, args);
    }
}
