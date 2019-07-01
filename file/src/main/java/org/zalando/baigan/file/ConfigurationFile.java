package org.zalando.baigan.file;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public final class ConfigurationFile {

    @JsonAnySetter
    private final Map<String, Namespace> namespaces = new HashMap<>();

    Map<String, Namespace> getNamespaces() {
        return namespaces;
    }

    final static class Namespace {
        @JsonAnySetter
        private final Map<String, ConfigurationHolder> holders = new HashMap<>();

        Map<String, ConfigurationHolder> getHolders() {
            return holders;
        }
    }

    final static class ConfigurationHolder {
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
    }
}
