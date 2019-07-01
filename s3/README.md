# Baigan configuration (S3 module)

[![Maven Central](https://img.shields.io/maven-central/v/org.zalando/baigan-config.svg)](https://maven-badges.herokuapp.com/maven-central/org.zalando/baigan-config)
[![Build Status](https://img.shields.io/travis/lukasniemeier-zalando/baigan-config/master.svg)](https://travis-ci.org/lukasniemeier-zalando/baigan-config)

Baigan configuration module allowing a `FileStores` to fetch *configuration files* from [AWS S3](https://aws.amazon.com/s3/) buckets
using your AWS credentials.

## Usage

Define your `ConfigurationStore` serving from a S3 bucket:

```java
class Example {
    static ConfigurationStore store() {
        return FileStores.builder()
                    .cached(Duration.ofMinutes(5))
                    .on(s3("bucket-name", "object-key"))
                    .asYaml();        
    }
}
```

See the [File](../file) module for further information on `FileStores`.

## Dependencies

* AWS Java SDK S3 1.11+
