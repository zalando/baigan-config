# Baigan configuration (etcd module)

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration module enabling [etcd](https://github.com/etcd-io/etcd) as the configuration backend.

* Serve complete *configuration files* from an etcd cluster (via `FilesStores` of the [File](../file) module).
* Serve individual configuration entries from an etcd cluster.

## Usage

Define your `ConfigurationStore` serving individual configuration entries from an etcd cluster:

```java
class Example {
    static ConfigurationStore store() {
        return EtcdStores.builder()
                    .cached(Duration.ofMinutes(15))
                    .on(URI.create("http://etcd/v2/keys/"))
                    .asYaml();
    }
}
```

In above's example a configuration in namespace `Business` with key `feature` will be resolved via `"http://etcd/v2/keys/Business.feature"`. 
A valid example configuration stored in etcd looks like this:

```yaml
value: true
description: My example business feature toggle.
```

Such configuration entries must adhere to the following JSON schema:

```json
{
    "$schema": "http://json-schema.org/draft-07/schema#",
    "description": "A baigan configuration entry.",
    "properties": {
        "description": {
            "description": "Summary of the configuration.",
            "type": "string"
        },
        "value": {
            "description": "The configuration's value."
        }
    },
    "required": [
        "value",
        "description"
    ],
    "type": "object"
}
```

### Serving *configuration files*

Alternatively you may want to serve a complete *configuration file* from your etcd cluster. 
This module provides an integration with the [File](../file) module to support this:

```java
class Example {
    static ConfigurationStore store() {
        return FileStores.builder()
                    .cached(ofMinutes(25))
                    .on(etcd(URI.create("http://etcd/v2/keys/configuration")))
                    .asYaml();
    }
}
```

Above's example expects a *configuration file* serialized as YAML at etcd key `configuration`.

## Dependencies

* Jackson Databind 2.9+
* Jackson Dataformat YAML 2.9+
