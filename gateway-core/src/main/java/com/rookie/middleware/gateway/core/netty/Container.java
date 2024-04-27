package com.rookie.middleware.gateway.core.netty;

import com.rookie.middleware.gateway.core.config.Config;
import com.rookie.middleware.gateway.core.lifeCycle.LifeCycle;
import com.rookie.middleware.gateway.core.netty.client.NettyHttpClient;
import com.rookie.middleware.gateway.core.netty.processor.NettyCoreProcessor;
import com.rookie.middleware.gateway.core.netty.processor.NettyProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
@Slf4j
public class Container implements LifeCycle {
    private final Config config;

    private NettyHttpServer nettyHttpServer;

    private NettyHttpClient nettyHttpClient;

    private NettyProcessor nettyProcessor;

    public Container(Config config) {
        this.config = config;
        init();
    }
    @Override
    public void init() {
        this.nettyProcessor = new NettyCoreProcessor();

        this.nettyHttpServer = new NettyHttpServer(config, nettyProcessor);

        this.nettyHttpClient = new NettyHttpClient(config,
                nettyHttpServer.getEventLoopGroupWorker());
    }

    @Override
    public void start() {
        nettyHttpServer.start();;
        nettyHttpClient.start();
        log.info("api gateway started!");
    }

    @Override
    public void shutdown() {
        nettyHttpServer.shutdown();
        nettyHttpClient.shutdown();
    }
}
