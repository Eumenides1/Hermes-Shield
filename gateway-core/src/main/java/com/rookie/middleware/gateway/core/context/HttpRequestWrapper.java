package com.rookie.middleware.gateway.core.context;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.Data;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */

@Data
public class HttpRequestWrapper {
    private FullHttpRequest request;
    private ChannelHandlerContext ctx;
}
