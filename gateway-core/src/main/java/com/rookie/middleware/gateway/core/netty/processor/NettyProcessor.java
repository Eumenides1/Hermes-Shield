package com.rookie.middleware.gateway.core.netty.processor;

import com.rookie.middleware.gateway.core.context.HttpRequestWrapper;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public interface NettyProcessor {

    void process(HttpRequestWrapper wrapper);
}