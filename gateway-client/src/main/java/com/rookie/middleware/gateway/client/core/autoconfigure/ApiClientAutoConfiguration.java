package com.rookie.middleware.gateway.client.core.autoconfigure;

import com.rookie.middleware.gateway.client.core.common.config.ApiProperties;
import com.rookie.middleware.gateway.client.core.common.support.dubbo.DubboClientRegisterManager;
import com.rookie.middleware.gateway.client.core.common.support.springmvc.SpringMVCClientRegisterManager;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Servlet;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Configuration
@EnableConfigurationProperties(ApiProperties.class)
@ConditionalOnProperty(prefix = "api", name = {"registerAddress"})
public class ApiClientAutoConfiguration {

    @Autowired
    private ApiProperties apiProperties;

    @Bean
    @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
    @ConditionalOnMissingBean(SpringMVCClientRegisterManager.class)
    public SpringMVCClientRegisterManager springMVCClientRegisterManager() {
        return new SpringMVCClientRegisterManager(apiProperties);
    }

    @Bean
    @ConditionalOnClass({ServiceBean.class})
    @ConditionalOnMissingBean(DubboClientRegisterManager.class)
    public DubboClientRegisterManager dubbo27ClientRegisterManager() {
        return new DubboClientRegisterManager(apiProperties);
    }
}
