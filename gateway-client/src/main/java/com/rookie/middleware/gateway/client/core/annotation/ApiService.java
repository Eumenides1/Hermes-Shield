package com.rookie.middleware.gateway.client.core.annotation;

import com.rookie.middleware.gateway.client.core.common.enums.ApiProtocol;

import java.lang.annotation.*;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiService {

    String serviceId();

    String version() default "1.0.0";

    ApiProtocol protocol();

    String patternPath();


}
