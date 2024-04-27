package com.rookie.middleware.gateway.core.filter.loadbalance;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */

import com.rookie.middleware.gateway.common.config.ServiceInstance;
import com.rookie.middleware.gateway.core.context.GatewayContext;

/**
 * 负载均衡顶级接口
 */
public interface IGatewayLoadBalanceRule {

    /**
     * 通过上下文参数获取服务实例
     * @param ctx
     * @return
     */
    ServiceInstance choose(GatewayContext ctx);

    /**
     * 通过服务ID拿到对应的服务实例
     * @param serviceId
     * @return
     */
    ServiceInstance choose(String serviceId);
}
