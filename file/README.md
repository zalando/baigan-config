# Baigan configuration (file module)

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration module providing a `ConfigurationStore`, which serves from a *configuration file*.

## Usage

Define your `ConfigurationStore` serving from a local file:

```java
class Example {
    static ConfigurationStore store() {
        return FileStores.builder()
                    .cached(Duration.ofMinutes(5))
                    .onLocalFile(Path.of("configuration.yaml"))
                    .asYaml();
    }
}
```

Such configuration file looks like this:

```yaml
ServiceConfiguration:
  serviceName:
    value: My Example Service
    description: Defining the service's name.
```

## Features

This module provides the following set of features:

* A `ConfigurationStore` serving from a *configuration file*.
* Fetching of *configuration files* from arbitrary sources (direct integration with a local `Path`, see the [S3 module](../s3) for fetching from S3 buckets).
* Mapping JSON and YAML *configuration files* to configuration objects (using the [Jackson](https://github.com/FasterXML/jackson) library).
* Built-in caching.

### Configuration File

By default this module is able to read *configuration files* either as JSON or YAML. 
Such configuration file must adhere to the following JSON schema:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "title": "Configuration File",
  "description": "A baigan configuration file.",
  "type": "object",
  "patternProperties": {
    "^[_a-zA-Z][_a-zA-Z0-9]*$": {
      "description": "A namespace holding configurations.",
      "type": "object",
      "patternProperties": {
        "^[_a-zA-Z][_a-zA-Z0-9]*$": {
          "description": "A configuration",
          "type": "object",
          "properties": {
            "value": {
              "description": "The configuration's value."
            },
            "description": {
              "description": "Summary of the configuration.",
              "type": "string"
            }
          },
          "required": [
            "value",
            "description"
          ],
          "additionalProperties": false
        }
      },
      "additionalProperties": false
    }
  },
  "additionalProperties": false
}
``` 

## Dependencies

* Jackson Databind 2.9+
* Jackson Dataformat YAML 2.9+
