package com.rookie.middleware.gateway.common.constants;

/**
 * @author eumenides
 * @description
 * @date 2024/4/24
 */
public interface GatewayProtocol {
    String HTTP = "http";

    String DUBBO = "dubbo";

    static boolean isHttp(String protocol) {
        return HTTP.equals(protocol);
    }

    static boolean isDubbo(String protocol) {
        return DUBBO.equals(protocol);
    }
}
