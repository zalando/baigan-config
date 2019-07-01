package org.zalando.baigan.etcd;

import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.zalando.baigan.CachedConfigurationStore;
import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.net.URI;
import java.time.Duration;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class EtcdStores {

    public static StoreBuilder builder() {
        return duration -> uri -> mapper -> new CachedConfigurationStore(
                duration,
                new EtcdConfigurationStore(
                        new EtcdValueFetcher(new ObjectMapper()),
                        mapper,
                        uri));
    }

    public interface StoreBuilder {
        UriBuilder cached(final Duration duration);

        interface UriBuilder {

            FormatBuilder on(final URI uri);

            interface FormatBuilder {
                ConfigurationStore as(final BiFunction<String, String, Configuration> mapper);

                default ConfigurationStore asJson() {
                    return as(new EtcdConfigurationHolderMapper(new ObjectMapper(new MappingJsonFactory())));
                }

                default ConfigurationStore asYaml() {
                    return as(new EtcdConfigurationHolderMapper(new ObjectMapper(new YAMLFactory())));
                }
            }
        }
    }

    private EtcdStores() {
        // utility class
    }

    public static Supplier<String> etcd(final URI uri) {
        return new EtcdConfigurationFileSupplier(
                new EtcdValueFetcher(new ObjectMapper()),
                uri);
    }

}
