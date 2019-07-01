package org.zalando.baigan.etcd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.zalando.baigan.Configuration;
import org.zalando.baigan.ConfigurationStore;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.net.URLEncoder.encode;
import static java.net.http.HttpRequest.BodyPublishers.ofString;
import static java.nio.charset.StandardCharsets.UTF_8;

@Testcontainers
class EtcdConfigurationStoreIntegrationTest {

    private static final int ETCD_PORT = 2379;

    @Container
    private final DockerComposeContainer container = new DockerComposeContainer(
            new File("src/test/resources/compose-etcd.yaml"))
            .withExposedService("etcd", ETCD_PORT);

    @Test
    void simpleTest() throws Exception {
        final String host = container.getServiceHost("etcd", ETCD_PORT);
        final int port = container.getServicePort("etcd", ETCD_PORT);

        final URI baseUri = URI.create("http://" + host + ":" + port + "/v2/keys/");
        final String expectedValue = "{'value':'foobar','description':'desc'}".replace('\'', '"');
        final String namespace = "ns1";
        final String key = "key1";

        putEtcdValue(baseUri.resolve(namespace + "." + key), expectedValue);

        final ObjectMapper mapper = new ObjectMapper();
        final ConfigurationStore store = EtcdStores.builder()
                .cached(Duration.ofSeconds(2))
                .on(baseUri)
                .asJson();
        final Optional<Configuration> configuration = store.getConfiguration(namespace, key);

        assertEquals("foobar", configuration.map(Configuration::getValue).orElse("nope"));
    }

    private void putEtcdValue(final URI uri, final String value) throws IOException, InterruptedException {
        final HttpClient client = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .PUT(ofString("value=" + encode(value, UTF_8)))
                .build();

        final Integer status = client.send(request, BodyHandlers.ofString()).statusCode();
        assertEquals(201, status);
    }

}
