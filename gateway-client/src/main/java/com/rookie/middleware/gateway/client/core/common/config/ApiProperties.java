package com.rookie.middleware.gateway.client.core.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
@Data
@ConfigurationProperties(prefix = "api")
public class ApiProperties {

    private String registerAddress;

    private String env = "dev";
}
