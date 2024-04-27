package com.rookie.middleware.gateway.core.filter.chain;

import com.rookie.middleware.gateway.common.rule.Rule;
import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.filter.Filter;
import com.rookie.middleware.gateway.core.filter.FilterFactory;
import com.rookie.middleware.gateway.core.filter.annotation.FilterAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Slf4j
public class GatewayFilterChainFactory  implements FilterFactory {

    private static class SingletonInstance{
        private static final GatewayFilterChainFactory INSTANCE = new GatewayFilterChainFactory();
    }

    public static GatewayFilterChainFactory getInstance(){
        return SingletonInstance.INSTANCE;
    }

    private Map<String, Filter> processorFilterIdMap = new ConcurrentHashMap<>();

    public GatewayFilterChainFactory() {
        ServiceLoader<Filter> serviceLoader = ServiceLoader.load(Filter.class);
        serviceLoader.stream().forEach(filterProvider -> {
            Filter filter = filterProvider.get();
            FilterAspect annotation = filter.getClass().getAnnotation(FilterAspect.class);
            log.info("load filter success:{},{},{},{}",filter.getClass(),
                    annotation.id(),annotation.name(),annotation.order());
            if(annotation != null){
                //添加到过滤集合
                String filterId = annotation.id();
                if(StringUtils.isEmpty(filterId)){
                    filterId = filter.getClass().getName();
                }
                processorFilterIdMap.put(filterId,filter);
            }
        });

    }
    public static void main(String[] args) {
        new GatewayFilterChainFactory();
    }


    @Override
    public GatewayFilterChain buildFilterChain(GatewayContext ctx) throws Exception {
        GatewayFilterChain chain = new GatewayFilterChain();
        List<Filter> filters = new ArrayList<>();
        Rule rule = ctx.getRule();
        if(rule != null){
            Set<Rule.FilterConfig> filterConfigs =   rule.getFilterConfigs();
            Iterator iterator = filterConfigs.iterator();
            Rule.FilterConfig filterConfig;
            while(iterator.hasNext()){
                filterConfig = (Rule.FilterConfig)iterator.next();
                if(filterConfig == null){
                    continue;
                }
                String filterId = filterConfig.getId();
                if(StringUtils.isNotEmpty(filterId) && getFilterInfo(filterId) != null){
                    Filter filter = getFilterInfo(filterId);
                    filters.add(filter);
                }
            }
        }
        //todo 添加路由过滤器-这是最后一步
        //filters.add(new RouterFilter());
        //排序
        filters.sort(Comparator.comparingInt(Filter::getOrder));
        //添加到链表中
        chain.addFilterList(filters);
        return chain;
    }

    @Override
    public Filter getFilterInfo(String filterId) throws Exception {
        return processorFilterIdMap.get(filterId);
    }
}
