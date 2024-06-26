package com.rookie.middleware.gateway.core;

import com.alibaba.fastjson.JSON;
import com.rookie.gateway.register.api.RegisterCenter;
import com.rookie.gateway.register.api.RegisterCenterListener;
import com.rookie.middleware.gateway.common.config.DynamicConfigManager;
import com.rookie.middleware.gateway.common.config.ServiceDefinition;
import com.rookie.middleware.gateway.common.config.ServiceInstance;
import com.rookie.middleware.gateway.common.constants.BasicConst;
import com.rookie.middleware.gateway.common.utils.NetUtils;
import com.rookie.middleware.gateway.common.utils.TimeUtil;
import com.rookie.middleware.gateway.configcenter.service.ConfigCenter;
import com.rookie.middleware.gateway.core.config.Config;
import com.rookie.middleware.gateway.core.config.ConfigLoader;
import com.rookie.middleware.gateway.core.netty.Container;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author eumenides
 * @description API网关启动类
 * @date 2024/4/13
 */
@Slf4j
public class Bootstrap {
    public static void main(String[] args) {
        // 加载网关核心静态配置
        //加载网关核心静态配置
        Config config = ConfigLoader.getInstance().load(args);
        System.out.println(config.getPort());

        System.out.println(config.getRegistryAddress());
        //插件初始化
        //配置中心管理器初始化，连接配置中心，监听配置的新增、修改、删除
        ServiceLoader<ConfigCenter> serviceLoader = ServiceLoader.load(ConfigCenter.class);
        final ConfigCenter configCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found ConfigCenter impl");
            return new RuntimeException("not found ConfigCenter impl");
        });
        configCenter.init(config.getRegistryAddress(), config.getEnv());
        configCenter.subscribeRulesChange(rules -> DynamicConfigManager.getInstance()
                .putAllRule(rules));

        // 启动容器
        Container container = new Container(config);
        container.start();
        // 加载注册中心
        //构造网关服务定义和服务实例
        //连接注册中心，将注册中心的实例加载到本地
        final RegisterCenter registerCenter = registerAndSubscribe(config);

        //服务优雅关机
        //收到kill信号时调用
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                registerCenter.deregister(buildGatewayServiceDefinition(config),
                        buildGatewayServiceInstance(config));
            }
        });
    }

    private static RegisterCenter registerAndSubscribe(Config config) {
        ServiceLoader<RegisterCenter> serviceLoader = ServiceLoader.load(RegisterCenter.class);
        final RegisterCenter registerCenter = serviceLoader.findFirst().orElseThrow(() -> {
            log.error("not found RegisterCenter impl");
            return new RuntimeException("not found RegisterCenter impl");
        });
        registerCenter.init(config.getRegistryAddress(), config.getEnv());

        //构造网关服务定义和服务实例
        ServiceDefinition serviceDefinition = buildGatewayServiceDefinition(config);
        ServiceInstance serviceInstance = buildGatewayServiceInstance(config);

        //注册
        registerCenter.register(serviceDefinition, serviceInstance);

        //订阅
        registerCenter.subscribeAllServices(new RegisterCenterListener() {
            @Override
            public void onChange(ServiceDefinition serviceDefinition, Set<ServiceInstance> serviceInstanceSet) {
                log.info("refresh service and instance: {} {}", serviceDefinition.getUniqueId(),
                        JSON.toJSON(serviceInstanceSet));
                DynamicConfigManager manager = DynamicConfigManager.getInstance();
                manager.addServiceInstance(serviceDefinition.getUniqueId(), serviceInstanceSet);
            }
        });
        return registerCenter;
    }


    private static ServiceInstance buildGatewayServiceInstance(Config config) {
        String localIp = NetUtils.getLocalIp();
        int port = config.getPort();
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setServiceInstanceId(localIp + BasicConst.COLON_SEPARATOR + port);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        return serviceInstance;
    }

    private static ServiceDefinition buildGatewayServiceDefinition(Config config) {
        ServiceDefinition serviceDefinition = new ServiceDefinition();
        serviceDefinition.setInvokerMap(Map.of());
        serviceDefinition.setUniqueId(config.getAppliactionName());
        serviceDefinition.setServiceId(config.getAppliactionName());
        serviceDefinition.setEnvType(config.getEnv());
        return serviceDefinition;
    }

}
