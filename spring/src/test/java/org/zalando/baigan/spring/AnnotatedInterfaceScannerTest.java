package org.zalando.baigan.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnnotatedInterfaceScannerTest {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface TestAnnotation {

    }

    @TestAnnotation
    private static class TestClass {

    }

    @TestAnnotation
    private interface TestInterface {

    }

    private final AnnotatedInterfaceScanner unit = new AnnotatedInterfaceScanner(TestAnnotation.class);

    @Test
    void filtersInterfaces() {
        final String packageName = AnnotatedInterfaceScannerTest.class.getPackage().getName();
        final Set<BeanDefinition> components = unit.findCandidateComponents(packageName);
        assertEquals(1, components.size());
        assertEquals(TestInterface.class.getName(), components.iterator().next().getBeanClassName());
    }

}