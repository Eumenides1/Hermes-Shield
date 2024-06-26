package com.rookie.middleware.gateway.core.helper;

import com.rookie.middleware.gateway.common.config.DynamicConfigManager;
import com.rookie.middleware.gateway.common.config.HttpServiceInvoker;
import com.rookie.middleware.gateway.common.config.ServiceDefinition;
import com.rookie.middleware.gateway.common.config.ServiceInvoker;
import com.rookie.middleware.gateway.common.constants.BasicConst;
import com.rookie.middleware.gateway.common.constants.GatewayConst;
import com.rookie.middleware.gateway.common.constants.GatewayProtocol;
import com.rookie.middleware.gateway.common.enums.ResponseCode;
import com.rookie.middleware.gateway.common.exception.ResponseException;
import com.rookie.middleware.gateway.common.rule.Rule;
import com.rookie.middleware.gateway.core.context.GatewayContext;
import com.rookie.middleware.gateway.core.request.GatewayRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public class RequestHelper {

    public static GatewayContext doContext(FullHttpRequest request, ChannelHandlerContext ctx) {

        //	构建请求对象GatewayRequest
        GatewayRequest gateWayRequest = doRequest(request, ctx);

        //	根据请求对象里的uniqueId，获取资源服务信息(也就是服务定义信息)
        ServiceDefinition serviceDefinition = ServiceDefinition.builder()
                .serviceId(gateWayRequest.getUniquedId())
                .enable(true)
                .version("v1")
                .patternPath("**")
                .envType("dev")
                .protocol(GatewayProtocol.HTTP)
                .build();

        //根据请求对象获取规则
        Rule rule = getRule(gateWayRequest,serviceDefinition.getServiceId());

        //	根据请求对象获取服务定义对应的方法调用，然后获取对应的规则
        ServiceInvoker serviceInvoker = new HttpServiceInvoker();
        serviceInvoker.setInvokerPath(gateWayRequest.getPath());
        serviceInvoker.setTimeout(500);


        //	构建我们而定GateWayContext对象
        GatewayContext gatewayContext = new GatewayContext(
                serviceDefinition.getProtocol(),
                ctx,
                HttpUtil.isKeepAlive(request),
                gateWayRequest,
                new Rule());


        return gatewayContext;
    }

    /**
     *构建Request请求对象
     */
    private static GatewayRequest doRequest(FullHttpRequest fullHttpRequest, ChannelHandlerContext ctx) {

        HttpHeaders headers = fullHttpRequest.headers();
        //	从header头获取必须要传入的关键属性 uniqueId
        String uniqueId = headers.get(GatewayConst.UNIQUE_ID);

        String host = headers.get(HttpHeaderNames.HOST);
        HttpMethod method = fullHttpRequest.method();
        String uri = fullHttpRequest.uri();
        String clientIp = getClientIp(ctx, fullHttpRequest);
        String contentType = HttpUtil.getMimeType(fullHttpRequest) == null ? null : HttpUtil.getMimeType(fullHttpRequest).toString();
        Charset charset = HttpUtil.getCharset(fullHttpRequest, StandardCharsets.UTF_8);

        GatewayRequest gatewayRequest = new GatewayRequest(uniqueId,
                charset,
                clientIp,
                host,
                uri,
                method,
                contentType,
                headers,
                fullHttpRequest);

        return gatewayRequest;
    }

    /**
     * 获取客户端ip
     */
    private static String getClientIp(ChannelHandlerContext ctx, FullHttpRequest request) {
        String xForwardedValue = request.headers().get(BasicConst.HTTP_FORWARD_SEPARATOR);

        String clientIp = null;
        if(StringUtils.isNotEmpty(xForwardedValue)) {
            List<String> values = Arrays.asList(xForwardedValue.split(", "));
            if(values.size() >= 1 && StringUtils.isNotBlank(values.get(0))) {
                clientIp = values.get(0);
            }
        }
        if(clientIp == null) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress)ctx.channel().remoteAddress();
            clientIp = inetSocketAddress.getAddress().getHostAddress();
        }
        return clientIp;
    }

    /**
     * 获取Rule对象
     * @param gateWayRequest
     * @return
     */
    private  static Rule getRule(GatewayRequest gateWayRequest,String serviceId){
        String key = serviceId + "." + gateWayRequest.getPath();
        Rule rule = DynamicConfigManager.getInstance().getRuleByPath(key);

        if (rule != null){
            return rule;
        }
        return DynamicConfigManager.getInstance().getRuleByServiceId(serviceId)
                .stream().filter(r -> gateWayRequest.getPath().startsWith(r.getPrefix()))
                .findAny().orElseThrow(()-> new ResponseException(ResponseCode.PATH_NO_MATCHED));
    }


}

