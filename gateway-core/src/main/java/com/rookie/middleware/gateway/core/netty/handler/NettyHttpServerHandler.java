package com.rookie.middleware.gateway.core.netty.handler;

import com.rookie.middleware.gateway.core.context.HttpRequestWrapper;
import com.rookie.middleware.gateway.core.netty.processor.NettyProcessor;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public class NettyHttpServerHandler extends ChannelInboundHandlerAdapter {
    private final NettyProcessor nettyProcessor;

    public NettyHttpServerHandler(NettyProcessor nettyProcessor) {
        this.nettyProcessor = nettyProcessor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        HttpRequestWrapper httpRequestWrapper = new HttpRequestWrapper();
        httpRequestWrapper.setCtx(ctx);
        httpRequestWrapper.setRequest(request);

        nettyProcessor.process(httpRequestWrapper);
    }
}
