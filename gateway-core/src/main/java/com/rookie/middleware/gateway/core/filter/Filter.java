package com.rookie.middleware.gateway.core.filter;

import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.filter.annotation.FilterAspect;

/**
 * @author eumenides
 * @description 过滤器顶级接口
 * @date 2024/4/27
 */
public interface Filter {

    void doFilter(GatewayContext ctx) throws  Exception;

    default int getOrder(){
        FilterAspect annotation = this.getClass().getAnnotation(FilterAspect.class);
        if(annotation != null){
            return annotation.order();
        }
        return Integer.MAX_VALUE;
    };


}
