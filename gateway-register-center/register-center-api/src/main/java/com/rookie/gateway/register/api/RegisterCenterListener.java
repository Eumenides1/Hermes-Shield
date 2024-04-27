package com.rookie.gateway.register.api;

import com.rookie.middleware.gateway.common.config.ServiceDefinition;
import com.rookie.middleware.gateway.common.config.ServiceInstance;

import java.util.Set;

/**
 * @author eumenides
 * @description
 * @date 2024/4/25
 */
public interface RegisterCenterListener {

    void onChange(ServiceDefinition serviceDefinition,
                  Set<ServiceInstance> serviceInstanceSet);
}
