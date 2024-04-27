package com.rookie.middleware.gateway.client.core.common.enums;

import lombok.Getter;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Getter
public enum ApiProtocol {
    HTTP("http","HTTP协议"),
    DUBBO("dubbo","dubbo协议");

    private String code;

    private String desc;

    ApiProtocol(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
