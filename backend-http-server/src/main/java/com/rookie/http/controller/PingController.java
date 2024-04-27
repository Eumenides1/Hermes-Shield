package com.rookie.http.controller;

import com.rookie.middleware.gateway.client.core.annotation.ApiInvoker;
import com.rookie.middleware.gateway.client.core.annotation.ApiService;
import com.rookie.middleware.gateway.client.core.common.config.ApiProperties;
import com.rookie.middleware.gateway.client.core.common.enums.ApiProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Slf4j
@RestController
@ApiService(serviceId = "backend-http-server", protocol = ApiProtocol.HTTP, patternPath = "/http-server/**")
public class PingController {

    @Autowired
    private ApiProperties apiProperties;

    @ApiInvoker(path = "/http-server/ping")
    @GetMapping("/http-server/ping")
    public String ping() {
        log.info("{}", apiProperties);
        return "pong";
    }

}

