package org.zalando.baigan.spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.zalando.baigan.ConfigurationStore;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static java.util.Arrays.asList;

@SpringJUnitConfig
class BaiganConfigurationIntegrationTest {

    interface SomeBaseConfiguration {
        String baseProperty();
    }

    @BaiganConfiguration
    interface SomeConfiguration extends SomeBaseConfiguration {
        Boolean someBoolean();

        String someString();

        String someFailingString();

        Integer someInteger();

        Long someLong();

        BigDecimal someDecimal();

        DayOfWeek someEnum();

        DayOfWeek someFailingEnum();

        long somePrimitiveLong();

        int someFailingPrimitiveInt();

        List<String> someListOfStrings();
    }

    @EnableBaigan
    @Configuration
    static class TestConfiguration {

        @Bean
        public ConfigurationStore myStore() {
            final InMemoryConfigurationStore store = new InMemoryConfigurationStore();
            store.addConfiguration("SomeConfiguration.someBoolean", false);
            store.addConfiguration("SomeConfiguration.someString", "foobar");
            store.addConfiguration("SomeConfiguration.someFailingString", Boolean.TRUE);
            store.addConfiguration("SomeConfiguration.someInteger", 12);
            store.addConfiguration("SomeConfiguration.someLong", 12L);
            store.addConfiguration("SomeConfiguration.someDecimal", "4.2");
            store.addConfiguration("SomeConfiguration.someEnum", "TUESDAY");
            store.addConfiguration("SomeConfiguration.someFailingEnum", "DOENERTAG");
            store.addConfiguration("SomeConfiguration.somePrimitiveLong", 42);
            store.addConfiguration("SomeConfiguration.someFailingPrimitiveInt", 42L);
            store.addConfiguration("SomeConfiguration.someListOfStrings", asList("one", "two"));
            store.addConfiguration("SomeConfiguration.baseProperty", "derived");
            return store;
        }

    }

    @Autowired(required = false)
    private SomeConfiguration configuration;

    @BeforeEach
    void setUp() {
        assertNotNull(configuration);
    }

    @Test
    void testBoolean() {
        assertEquals(false, configuration.someBoolean());
    }

    @Test
    void testString() {
        assertEquals("foobar", configuration.someString());
    }

    @Test
    void testFailingString() {
        assertNull(configuration.someFailingString());
    }

    @Test
    void testInteger() {
        assertEquals(12, configuration.someInteger());
    }

    @Test
    void testLong() {
        assertEquals(12L, configuration.someLong());
    }

    @Test
    void testPrimitive() {
        assertEquals(42L, configuration.somePrimitiveLong());
    }

    @Test
    void testFailingPrimitive() {
        // fails due to falling back to `null` on primitive type
        assertThrows(NullPointerException.class, () -> System.out.println("" + configuration.someFailingPrimitiveInt()));
    }

    @Test
    void testEnum() {
        assertEquals(DayOfWeek.TUESDAY, configuration.someEnum());
    }

    @Test
    void testFailingEnum() {
        assertNull(configuration.someFailingEnum());
    }

    @Test
    void testBigDecimal() {
        assertEquals(new BigDecimal("4.2"), configuration.someDecimal());
    }

    @Test
    void testListOfStrings() {
        assertEquals(asList("one", "two"), configuration.someListOfStrings());
    }

    @Test
    void testBaseProperty() {
        assertEquals("derived", configuration.baseProperty());
    }

}
