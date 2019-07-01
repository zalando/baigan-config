package org.zalando.baigan.etcd;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;

class EtcdValueFetcher implements Function<URI, Optional<String>> {

    private final ObjectMapper mapper;

    EtcdValueFetcher(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<String> apply(final URI uri) {
        try {
            final ResultNode node = mapper.readValue(uri.toURL(), ResultNode.class);
            return Optional.of(node.getNode().getValue());
        } catch (final FileNotFoundException e) {
            return Optional.empty();
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static final class ResultNode {

        private final String action;
        private final KeyNode node;

        @JsonCreator
        ResultNode(@JsonProperty("action") final String action, @JsonProperty("node") final KeyNode node) {
            this.action = action;
            this.node = node;
        }

        String getAction() {
            return action;
        }

        KeyNode getNode() {
            return node;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static final class KeyNode {

            private final String value;

            @JsonCreator
            KeyNode(@JsonProperty("value") final String value) {
                this.value = value;
            }

            String getValue() {
                return value;
            }
        }
    }
}
