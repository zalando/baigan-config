package org.zalando.baigan.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URLConnection;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

final class UriConfigurationFileSupplier implements Supplier<String> {

    private final URI uri;

    UriConfigurationFileSupplier(final URI uri) {
        this.uri = uri;
    }

    @Override
    public String get() {
        try {
            final URLConnection connection = uri.toURL().openConnection();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
