package org.zalando.baigan.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zalando.baigan.example.configuration.ServiceConfiguration;
import javax.annotation.PostConstruct;

@Component
class Service {

    private final Logger LOG = LoggerFactory.getLogger(Service.class);

    private final ServiceConfiguration configuration;

    Service(final ServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @PostConstruct
    public void report() {
        LOG.info("Hello world, this is [{}].", configuration.serviceName());
    }
}
