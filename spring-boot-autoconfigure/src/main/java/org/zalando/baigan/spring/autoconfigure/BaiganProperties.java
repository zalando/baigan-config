package org.zalando.baigan.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "baigan", ignoreUnknownFields = false)
class BaiganProperties implements Validator {

    private final Validator validator = new StorePropertiesValidator();

    @NestedConfigurationProperty
    private StoreProperties store;

    public StoreProperties getStore() {
        return store;
    }

    public void setStore(final StoreProperties store) {
        this.store = store;
    }

    @Override
    public boolean supports(final Class<?> clazz) {
        return validator.supports(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        validator.validate(target, errors);
    }

}
