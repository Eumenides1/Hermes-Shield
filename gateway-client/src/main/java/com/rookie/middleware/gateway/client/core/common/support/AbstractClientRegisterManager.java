package com.rookie.middleware.gateway.client.core.common.support;

import com.rookie.gateway.register.api.RegisterCenter;
import com.rookie.middleware.gateway.client.core.common.config.ApiProperties;
import com.rookie.middleware.gateway.common.config.ServiceDefinition;
import com.rookie.middleware.gateway.common.config.ServiceInstance;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ServiceLoader;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Slf4j
public class AbstractClientRegisterManager {
    @Getter
    private ApiProperties apiProperties;

    private RegisterCenter registerCenter;

    protected AbstractClientRegisterManager(ApiProperties apiProperties) {
        this.apiProperties = apiProperties;

        //初始化注册中心对象
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
    }

    protected void register(ServiceDefinition serviceDefinition, ServiceInstance serviceInstance) {
        registerCenter.register(serviceDefinition, serviceInstance);
    }
}
