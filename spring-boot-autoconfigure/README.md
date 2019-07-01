# Baigan configuration (Spring Boot module)

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration module providing a Spring Boot AutoConfiguration.

## Usage

Using this module, Baigan configuration is active out-of-the-box.
All you need to do is to define your `ConfigurationStore` bean, 
either by programmatically providing a `@Bean` or via the `application.yaml`:

```
baigan:
  store:
    type: namespaced
    stores:
      FeatureAlpha:
        type: file
        cache: 2m
        location: file.json
        format: json
      FeatureBeta:
        type: chained
        stores:
          - type: file
            lazy: true
            location: s3://my-bucket/my-key
            format: yaml
          - type: etcd
            style: configuration-file
            cache: PT15M
            location: http://localhost/v2/keys/configuration
            format: yaml
      FeatureGamma:
        type: etcd
        style: configuration-key
        lazy: true
        cache: PT2M
        location: http://localhost/v2/keys/
        format: yaml
```

## Configuration

You can define your `ConfigurationStore` via the `application.yaml` (see example above).

| type of store | key      | type     | default | description                                                |
|---------------|----------|----------|---------|------------------------------------------------------------|
| *any*         | lazy     | boolean  | false   | Indicates whether this store should be initialized lazily. |
| chained       | stores   | List     |         | List of stores to be chained.                              |
| namespaced    | stores   | Map      |         | Map of namespace to store.                                 |
| file          | cache    | Duration | 2 min   | Duration the configuration file is cached.                 |
| file          | format   | Format   | JSON    | Format of the configuration file (JSON or YAML).           |
| file          | location | String   |         | URI¹ of the configuration file.                            |
| etcd          | cache    | Duration | 2 min   | Duration the configuration is cached.                      |
| etcd          | format   | Format   | JSON    | Format of the configuration.                               |
| etcd          | location | String   |         | URI of the etcd key/etcd cluster.                          |
| etcd          | style    | Style    |         | Either `configuration-file` or `configuration-key`².       |

(¹) any non absolute URI is interpreted as a local file path.

(²) style `configuration-file` assume a full [*configuration file*](../file) at the given location, 
style `configuration-key` results in resolving the given location with the individual namespace and key.

## Dependencies

* Spring Boot 2.1+
