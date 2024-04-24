package com.rookie.middleware.gateway.common.config;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public interface ServiceInvoker {

    /**
     * 获取真正的服务调用的全路径
     */
    String getInvokerPath();

    void setInvokerPath(String invokerPath);

    /**
     * 获取该服务调用(方法)的超时时间
     */
    int getTimeout();

    void setTimeout(int timeout);

}

