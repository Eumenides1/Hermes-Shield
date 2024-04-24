package com.rookie.middleware.gateway.core.lifeCycle;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public interface LifeCycle {
    /**
     * 初始化
     */
    void init();

    /**
     * 启动
     */
    void start();

    /**
     * 关闭
     */
    void shutdown();
}
