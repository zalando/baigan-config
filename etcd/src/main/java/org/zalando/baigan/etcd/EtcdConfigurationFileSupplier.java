package org.zalando.baigan.etcd;

import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

final class EtcdConfigurationFileSupplier implements Supplier<String> {

    private final Function<URI, Optional<String>> valueFetcher;
    private final URI uri;

    EtcdConfigurationFileSupplier(final Function<URI, Optional<String>> valueFetcher, final URI uri) {
        this.valueFetcher = valueFetcher;
        this.uri = uri;
    }

    @Override
    public String get() {
        return valueFetcher.apply(uri).orElseThrow(() -> new UncheckedIOException(new FileNotFoundException("No configuration file at given URI")));
    }
}
