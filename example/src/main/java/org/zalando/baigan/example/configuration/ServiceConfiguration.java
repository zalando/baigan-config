package org.zalando.baigan.example.configuration;

import org.zalando.baigan.spring.BaiganConfiguration;

@BaiganConfiguration
public interface ServiceConfiguration {

    String serviceName();

}
