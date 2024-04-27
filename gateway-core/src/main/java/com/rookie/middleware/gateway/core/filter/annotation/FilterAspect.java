package com.rookie.middleware.gateway.core.filter.annotation;

import java.lang.annotation.*;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FilterAspect {
    /**
     * 过滤器ID
     * @return
     */
    String id();

    /**
     * 过滤器名称
     * @return
     */
    String name() default "";

    /**
     * 排序
     * @return
     */
    int order() default 0;

}
