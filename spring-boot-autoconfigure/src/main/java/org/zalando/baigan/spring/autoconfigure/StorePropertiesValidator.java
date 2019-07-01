package org.zalando.baigan.spring.autoconfigure;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.springframework.beans.BeanUtils.getPropertyDescriptor;
import static java.util.stream.Stream.of;

final class StorePropertiesValidator implements Validator {

    @Override
    public boolean supports(final Class<?> clazz) {
        return clazz.equals(StoreProperties.class);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final StoreProperties properties = (StoreProperties) target;

        final StoreProperties.StoreType type = properties.getType();
        if (type == null) {
            reportRequiredField(errors, "type");
            return;
        }

        of(StoreProperties.class.getDeclaredFields()).forEach(field -> {
            try {
                final String fieldName = field.getName();
                final boolean required = type.getRequiredFields().contains(fieldName);
                final boolean forbidden = !type.getRequiredFields().contains(fieldName) && !type.getOptionalFields().contains(fieldName);

                final PropertyDescriptor property = getPropertyDescriptor(StoreProperties.class, fieldName);
                final Object value = property.getReadMethod().invoke(properties);

                if (required && value == null) {
                    reportRequiredField(errors, fieldName);
                }
                if (forbidden && value != null) {
                    reportUnexpectedField(errors, fieldName);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static void reportRequiredField(final Errors errors, final String fieldName) {
        errors.rejectValue(fieldName, "field.required", "field is required");
    }

    private static void reportUnexpectedField(final Errors errors, final String fieldName) {
        errors.rejectValue(fieldName, "field.unexpected", "unexpected field");
    }
}
