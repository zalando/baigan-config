# Baigan configuration (Spring module)

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration module enabling access to your configuration by simply annotating an interface.

## Usage

This module registers a Spring *bean* for each interface annotated with `@BaiganConfiguration`.

```java
@BaiganConfiguration
interface ServiceConfiguration {
    String serviceName();
}

@Component
class Service {

    @Autowired
    private ServiceConfiguration configuration;

    public String whoAmI() {
        return configuration.serviceName();
    }
}

```

In order to install such dynamic configuration *beans* make sure to activate the module via `@EnableBaigan` and provide a `ConfigurationStore` bean:

```java
@EnableBaigan
@Configuration
class ServiceConfiguration {
    @Bean
    ConfigurationStore configurationStore() {
        return FileStores.builder()
                   .cached(Duration.ofMinutes(5))
                   .onLocalFile(of("configuration.json"))
                   .asJson();
    }
}
```

By default the package of the `@Configuration` class annotated with `@EnableBaigain` 
is used as the base package to scan for `@BaiganConfiguration` annotated interfaces.

## Dependencies

* Spring Context 5+