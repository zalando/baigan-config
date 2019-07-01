# Baigan configuration

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration is an easy to use framework for Java applications allowing you to store and fetch runtime configuration.

Its simple but extensible interfaces allow for powerful integrations.

* fetch your configuration from arbitrary sources serving any format (e.g. local files, S3, etcd ...)
* integrate with the [Spring Framework](https://spring.io/) enabling access to your configuration by simply annotating an interface.
* integrate with Baigan configuration in seconds, via the provided [Spring Boot](https://spring.io/projects/spring-boot) Auto-configuration library

## Usage

The following example makes use of the [File](file), [S3](s3), [etcd](etcd) and [Spring Boot](spring-boot-autoconfigure) module.

Usage of Baigan configuration is as easy as:

```
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

Serving of such runtime configuration is done via a simple `configuration.yaml`:

```
ServiceConfiguration:
  serviceName:
    value: My Example Service
    description: Defining the service's name.
```

Ultimately add the following to your `application.yaml`:

```
baigan:
  store:
    type: local-file
    cache: PT1M
    path: configuration.yaml
    format: yaml
```

## Concepts

At its core, Baigan configuration defines two entities.

- `Configuration` represents the runtime configuration. It's accessible by its `key` and holds a corresponding `value`. 
In order to facilitate documentation, a configuration object must have a `description`.
- `ConfigurationStore` represents a collection of configuration objects. 
A configuration store is organizing the held configuration objects in *namespaces*.

Implementations of `ConfigurationStore` may serve configuration objects from arbitrary sources, e.g. memory, local files, S3 buckets, databases.
Looking up a `Configuration` by its key, may involve traversing a *chain* of `ConfigurationStores` (allowing you to define *fallback* stores) or
is routed by the *namespace* (allowing you to define different sources per namespace).

## Integrations

Baigan configuration comes with a set of powerful integrations.

* The [Spring](spring) module enables accessing configuration via annotated interfaces in Spring applications.
* The [Spring Boot](spring-boot-autoconfigure) module simplifies configuration in Spring applications.
* The [File](file) module provides a `ConfigurationStore` implementation serving from JSON and YAML files.
* The [S3](s3) module allows for fetching configuration files from S3 buckets.
* The [etcd](etcd) module allows using an etcd cluster as the configuration backend.

## Migration

If you wish to migrate your Spring application from Baigan configuration `0.14.x` and lower there are a few steps to follow.

1. Transform your current Baigan *configuration files* into the new format. 

You may start with the following [jq](https://stedolan.github.io/jq/manual/) script. What's left is "extracting" your namespaces. 

```
$ jq '.[] | { (.alias): { description: .description, value: .defaultValue } }'
```

See the [File](file) module for more information about the new format.

2. Remove usage of the old Spring annotations referencing `BaiganSpringContext` and `ConfigurationServiceScan`. 
Also rename any usage of `@BaiganConfig` to `@BaiganConfiguration`.

3. Replace any definition of a `ConfigurationRepository` bean with a `ConfigurationStore`. 

4. Register Baigan configuration in your Spring context
    
    a. Either manually via `@EnableBaigan` of the [Spring](spring) module...
    
    b. ... or automatically via the  [Spring Boot](spring-boot-autoconfigure) module.
    
**Remarks**:

Currently not yet supported features of Baigan configuration `0.14.x` are:

* context-dependent configuration values via "conditions".
* serving *configuration files* from Git repositories.

## Development

To build the project run

```bash
$ ./gradlew clean check
```

Be aware that the build requires Docker.
