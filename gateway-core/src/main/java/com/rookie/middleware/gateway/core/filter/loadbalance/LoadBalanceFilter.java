package com.rookie.middleware.gateway.core.filter.loadbalance;

import com.alibaba.fastjson.JSON;
import com.rookie.middleware.gateway.common.config.ServiceInstance;
import com.rookie.middleware.gateway.common.exception.NotFoundException;
import com.rookie.middleware.gateway.common.rule.Rule;
import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.filter.Filter;
import com.rookie.middleware.gateway.core.filter.annotation.FilterAspect;
import com.rookie.middleware.gateway.core.request.GatewayRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.rookie.middleware.gateway.common.constants.FilterConst.*;

import static com.rookie.middleware.gateway.common.enums.ResponseCode.SERVICE_INSTANCE_NOT_FOUND;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Slf4j
@FilterAspect(id=LOAD_BALANCE_FILTER_ID,
        name = LOAD_BALANCE_FILTER_NAME,
        order = LOAD_BALANCE_FILTER_ORDER)
public class LoadBalanceFilter implements Filter {
    @Override
    public void doFilter(GatewayContext ctx){
        String serviceId = ctx.getUniqueId();
        IGatewayLoadBalanceRule gatewayLoadBalanceRule = getLoadBalanceRule(ctx);
        ServiceInstance serviceInstance = gatewayLoadBalanceRule.choose(serviceId);
        System.out.println("IP为"+serviceInstance.getIp()+",端口号："+serviceInstance.getPort());
        GatewayRequest request = ctx.getRequest();
        if(serviceInstance != null && request != null){
            String host  = serviceInstance.getIp()+":"+serviceInstance.getPort();
            request.setModifyHost(host);
        }else{
            log.warn("No instance available for :{}",serviceId);
            throw new NotFoundException(SERVICE_INSTANCE_NOT_FOUND);
        }
    }


    /**
     * 根据配置获取负载均衡器
     *
     * @param ctx
     * @return
     */
    public IGatewayLoadBalanceRule getLoadBalanceRule(GatewayContext ctx) {
        IGatewayLoadBalanceRule loadBalanceRule = null;
        Rule configRule = ctx.getRule();
        if (configRule != null) {
            Set<Rule.FilterConfig> filterConfigs = configRule.getFilterConfigs();
            Iterator iterator = filterConfigs.iterator();
            Rule.FilterConfig filterConfig;
            while (iterator.hasNext()) {
                filterConfig = (Rule.FilterConfig) iterator.next();
                if (filterConfig == null) {
                    continue;
                }
                String filterId = filterConfig.getId();
                if (filterId.equals(LOAD_BALANCE_FILTER_ID)) {
                    String config = filterConfig.getConfig();
                    String strategy = LOAD_BALANCE_STRATEGY_RANDOM;
                    if (StringUtils.isNotEmpty(config)) {
                        Map<String, String> mapTypeMap = JSON.parseObject(config, Map.class);
                        strategy = mapTypeMap.getOrDefault(LOAD_BALANCE_KEY, strategy);
                    }
                    switch (strategy) {
                        case LOAD_BALANCE_STRATEGY_RANDOM:
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        case LOAD_BALANCE_STRATEGY_ROUND_ROBIN:
                            loadBalanceRule = RoundRobinLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                        default:
                            log.warn("No loadBalance strategy for service:{}", strategy);
                            loadBalanceRule = RandomLoadBalanceRule.getInstance(configRule.getServiceId());
                            break;
                    }
                }
            }
        }
        return loadBalanceRule;
    }
}
