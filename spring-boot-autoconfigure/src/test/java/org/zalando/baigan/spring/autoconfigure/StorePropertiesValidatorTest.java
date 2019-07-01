package org.zalando.baigan.spring.autoconfigure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.validation.Errors;
import org.zalando.baigan.ConfigurationStore;
import java.time.Duration;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.Format.JSON;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.StoreType.CHAINED;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.StoreType.ETCD;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.StoreType.FILE;
import static org.zalando.baigan.spring.autoconfigure.StoreProperties.StoreType.NAMESPACED;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

class StorePropertiesValidatorTest {

    private final Errors errors = mock(Errors.class);
    private final StorePropertiesValidator unit = new StorePropertiesValidator();

    @Test
    void supportsStoreProperties() {
        assertTrue(unit.supports(StoreProperties.class));
        assertFalse(unit.supports(ConfigurationStore.class));
    }

    @Test
    void requiresType() {
        unit.validate(new StoreProperties(), errors);
        verify(errors).rejectValue(eq("type"), anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource
    void validatesEachStoreType(final TestCase testCase) {

        final StoreProperties props = new StoreProperties();
        testCase.builder.accept(props);

        unit.validate(props, errors);

        testCase.unexpectedField.forEach(field -> verify(errors).rejectValue(eq(field), anyString(), contains("unexpected")));
        testCase.missingFields.forEach(field -> verify(errors).rejectValue(eq(field), anyString(), contains("required")));
        verifyNoMoreInteractions(errors);
    }

    @SuppressWarnings("unused")
    static Stream<TestCase> validatesEachStoreType() {
        return Stream.of(
                new TestCase(NAMESPACED, emptyList(), emptyList(), props -> {
                    props.setStores(emptyMap());
                }),
                new TestCase(NAMESPACED, asList("style", "location"), singletonList("stores"), props -> {
                    props.setStyle(StoreProperties.EtcdStyle.CONFIGURATION_FILE);
                    props.setLocation("value");
                }),

                new TestCase(CHAINED, emptyList(), emptyList(), props -> {
                    props.setStores(emptyMap());
                }),
                new TestCase(CHAINED, asList("style", "location"), singletonList("stores"), props -> {
                    props.setStyle(StoreProperties.EtcdStyle.CONFIGURATION_FILE);
                    props.setLocation("value");
                }),

                new TestCase(ETCD, emptyList(), emptyList(), props -> {
                    props.setLocation("value");
                    props.setStyle(StoreProperties.EtcdStyle.CONFIGURATION_FILE);
                }),
                new TestCase(ETCD, emptyList(), emptyList(), props -> {
                    props.setLocation("value");
                    props.setStyle(StoreProperties.EtcdStyle.CONFIGURATION_FILE);
                    props.setCache(Duration.ZERO);
                    props.setFormat(JSON);
                }),
                new TestCase(ETCD, singletonList("stores"), asList("location", "style"), props -> {
                    props.setStores(emptyMap());
                }),

                new TestCase(FILE, emptyList(), emptyList(), props -> {
                    props.setLocation("value");
                }),
                new TestCase(FILE, emptyList(), emptyList(), props -> {
                    props.setLocation("value");
                    props.setCache(Duration.ZERO);
                    props.setFormat(JSON);
                }),
                new TestCase(FILE, singletonList("style"), singletonList("location"), props -> {
                    props.setStyle(StoreProperties.EtcdStyle.CONFIGURATION_FILE);
                })
        );
    }

    static class TestCase {
        final StoreProperties.StoreType type;
        final List<String> unexpectedField;
        final List<String> missingFields;
        final Consumer<StoreProperties> builder;

        TestCase(final StoreProperties.StoreType type,
                final List<String> unexpectedField,
                final List<String> missingFields,
                final Consumer<StoreProperties> builder) {
            this.type = type;
            this.unexpectedField = unexpectedField;
            this.missingFields = missingFields;
            this.builder = props -> {
                props.setType(type);
                builder.accept(props);
            };
        }

        @Override
        public String toString() {
            return new StringJoiner(", ")
                    .add("type=" + type)
                    .add("unexpected=" + unexpectedField)
                    .add("missing=" + missingFields)
                    .toString();
        }
    }
}