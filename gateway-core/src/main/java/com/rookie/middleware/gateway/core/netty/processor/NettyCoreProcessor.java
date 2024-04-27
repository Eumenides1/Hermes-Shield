package com.rookie.middleware.gateway.core.netty.processor;

import com.rookie.middleware.gateway.common.enums.ResponseCode;
import com.rookie.middleware.gateway.common.exception.BaseException;
import com.rookie.middleware.gateway.common.exception.ConnectException;
import com.rookie.middleware.gateway.common.exception.ResponseException;
import com.rookie.middleware.gateway.core.config.ConfigLoader;
import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.context.HttpRequestWrapper;
import com.rookie.middleware.gateway.core.filter.FilterFactory;
import com.rookie.middleware.gateway.core.filter.chain.GatewayFilterChainFactory;
import com.rookie.middleware.gateway.core.helper.AsyncHttpHelper;
import com.rookie.middleware.gateway.core.helper.RequestHelper;
import com.rookie.middleware.gateway.core.helper.ResponseHelper;
import com.rookie.middleware.gateway.core.response.GatewayResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
@Slf4j
public class NettyCoreProcessor implements NettyProcessor{
    private FilterFactory filterFactory = GatewayFilterChainFactory.getInstance();
    @Override
    public void process(HttpRequestWrapper wrapper) {
        FullHttpRequest request = wrapper.getRequest();
        ChannelHandlerContext ctx = wrapper.getCtx();

        try {
            GatewayContext gatewayContext = RequestHelper.doContext(request, ctx);
            filterFactory.buildFilterChain(gatewayContext).doFilter(gatewayContext);
            route(gatewayContext);
        } catch (BaseException e) {
            log.error("process error {} {}", e.getCode().getCode(), e.getCode().getMessage());
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(e.getCode());
            doWriteAndRelease(ctx, request, httpResponse);
        } catch (Throwable t) {
            log.error("process unkown error", t);
            FullHttpResponse httpResponse = ResponseHelper.getHttpResponse(ResponseCode.INTERNAL_ERROR);
            doWriteAndRelease(ctx, request, httpResponse);
        }
    }

    private void doWriteAndRelease(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse httpResponse) {
        //释放资源后关闭channel
        ctx.writeAndFlush(httpResponse)
                .addListener(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(request);
    }

    private void route(GatewayContext gatewayContext) {
        Request request = gatewayContext.getRequest().build();
        CompletableFuture<Response> future = AsyncHttpHelper.getInstance().executeRequest(request);

        boolean whenComplete = ConfigLoader.getConfig().isWhenComplete();

        if (whenComplete) {
            future.whenComplete((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        } else {
            future.whenCompleteAsync((response, throwable) -> {
                complete(request, response, throwable, gatewayContext);
            });
        }
    }

    private void complete(Request request,
                          Response response,
                          Throwable throwable,
                          GatewayContext gatewayContext) {
        gatewayContext.releaseRequest();

        try {
            if (Objects.nonNull(throwable)) {
                String url = request.getUrl();
                if (throwable instanceof TimeoutException) {
                    log.warn("complete time out {}", url);
                    gatewayContext.setThrowable(new ResponseException(ResponseCode.REQUEST_TIMEOUT));
                } else {
                    gatewayContext.setThrowable(new ConnectException(throwable,
                            gatewayContext.getUniqueId(),
                            url, ResponseCode.HTTP_RESPONSE_ERROR));
                }
            } else {
                gatewayContext.setResponse(GatewayResponse.buildGatewayResponse(response));
            }
        } catch (Throwable t) {
            gatewayContext.setThrowable(new ResponseException(ResponseCode.INTERNAL_ERROR));
            log.error("complete error", t);
        } finally {
            gatewayContext.written();
            ResponseHelper.writeResponse(gatewayContext);
        }
    }
}
