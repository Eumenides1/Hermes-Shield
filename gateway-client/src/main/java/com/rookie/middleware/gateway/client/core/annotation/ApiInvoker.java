package com.rookie.middleware.gateway.client.core.annotation;

import java.lang.annotation.*;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiInvoker {

    String path();


}
