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

    private String registryAddress = "49.232.228.92:8848";

    private String env = "dev";

    //netty
    private int eventLoopGroupBossNum = 1;

    // worker 线程数
    private int eventLoopGroupWorkerNum = Runtime.getRuntime().availableProcessors();

    private int maxContentLength = 64 * 1024 * 1024;

    //默认单异步模式
    private boolean whenComplete = true;

    //	Http Async 参数选项：

    //	连接超时时间
    private int httpConnectTimeout = 30 * 1000;

    //	请求超时时间
    private int httpRequestTimeout = 30 * 1000;

    //	客户端请求重试次数
    private int httpMaxRequestRetry = 2;

    //	客户端请求最大连接数
    private int httpMaxConnections = 10000;

    //	客户端每个地址支持的最大连接数
    private int httpConnectionsPerHost = 8000;

    //	客户端空闲连接超时时间, 默认60秒
    private int httpPooledConnectionIdleTimeout = 60 * 1000;

    //扩展.......
}