package com.rookie.middleware.gateway.core.config;

import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/14
 */

@Data
public class Config {
    private int port = 8888;

    private String appliactionName = "api-gateway";

    private String registryAddress = "127.0.0.1:8848";

    private String env = "dev";

    //netty
    private int eventLoopGroupBossNum = 1;

    // worker 线程数
    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();

    private int maxContentLength = 64 * 1024 * 1024;

    //默认单异步模式
    private boolean whenComplete = true;

    //扩展.......
}