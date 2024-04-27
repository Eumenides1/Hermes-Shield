package com.rookie.middleware.gateway.client.core.common.support.dubbo;

import com.rookie.middleware.gateway.client.core.ApiAnnotationScanner;
import com.rookie.middleware.gateway.client.core.common.config.ApiProperties;
import com.rookie.middleware.gateway.client.core.common.support.AbstractClientRegisterManager;
import com.rookie.middleware.gateway.common.config.ServiceDefinition;
import com.rookie.middleware.gateway.common.config.ServiceInstance;
import com.rookie.middleware.gateway.common.utils.NetUtils;
import com.rookie.middleware.gateway.common.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.event.ServiceBeanExportedEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.HashSet;
import java.util.Set;

import static com.rookie.middleware.gateway.common.constants.BasicConst.COLON_SEPARATOR;
import static com.rookie.middleware.gateway.common.constants.GatewayConst.DEFAULT_WEIGHT;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Slf4j
public class DubboClientRegisterManager extends AbstractClientRegisterManager implements ApplicationListener<ApplicationEvent> {

    private Set<Object> set = new HashSet<>();

    public DubboClientRegisterManager(ApiProperties apiProperties) {
        super(apiProperties);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if (applicationEvent instanceof ServiceBeanExportedEvent) {
            try {
                ServiceBean serviceBean = ((ServiceBeanExportedEvent) applicationEvent).getServiceBean();
                doRegisterDubbo(serviceBean);
            } catch (Exception e) {
                log.error("doRegisterDubbo error", e);
                throw new RuntimeException(e);
            }
        } else if (applicationEvent instanceof ApplicationStartedEvent) {
            log.info("dubbo api started");
        }
    }

    private void doRegisterDubbo(ServiceBean serviceBean) {
        Object bean = serviceBean.getRef();

        if (set.contains(bean)) {
            return;
        }

        ServiceDefinition serviceDefinition = ApiAnnotationScanner.getInstance().scanner(bean, serviceBean);

        if (serviceDefinition == null) {
            return;
        }

        serviceDefinition.setEnvType(getApiProperties().getEnv());

        //服务实例
        ServiceInstance serviceInstance = new ServiceInstance();
        String localIp = NetUtils.getLocalIp();
        int port = serviceBean.getProtocol().getPort();
        String serviceInstanceId = localIp + COLON_SEPARATOR + port;
        String uniqueId = serviceDefinition.getUniqueId();
        String version = serviceDefinition.getVersion();

        serviceInstance.setServiceInstanceId(serviceInstanceId);
        serviceInstance.setUniqueId(uniqueId);
        serviceInstance.setIp(localIp);
        serviceInstance.setPort(port);
        serviceInstance.setRegisterTime(TimeUtil.currentTimeMillis());
        serviceInstance.setVersion(version);
        serviceInstance.setWeight(DEFAULT_WEIGHT);

        register(serviceDefinition, serviceInstance);
    }
}
