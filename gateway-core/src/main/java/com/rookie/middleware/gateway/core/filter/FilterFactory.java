package com.rookie.middleware.gateway.core.filter;

import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.filter.chain.GatewayFilterChain;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
public interface FilterFactory {

    /**
     * 构建过滤器链条
     * @param ctx
     * @return
     * @throws Exception
     */
    GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception;

    /**
     * 通过过滤器ID获取过滤器
     * @param filterId
     * @return
     * @param <T>
     * @throws Exception
     */
    <T> T getFilterInfo(String filterId) throws Exception;
}