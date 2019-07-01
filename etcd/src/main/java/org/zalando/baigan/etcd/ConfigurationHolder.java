package org.zalando.baigan.etcd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

final class ConfigurationHolder {
    private final String description;
    private final Object value;

    @JsonCreator
    ConfigurationHolder(@JsonProperty("description") final String description, @JsonProperty("value") final Object value) {
        this.description = requireNonNull(description, "Required description is missing");
        this.value = requireNonNull(value, "Required value is missing");
    }

    String getDescription() {
        return description;
    }

    Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConfigurationHolder.class.getSimpleName() + "[", "]")
                .add("description='" + description + "'")
                .add("value=" + value)
                .toString();
    }
}
