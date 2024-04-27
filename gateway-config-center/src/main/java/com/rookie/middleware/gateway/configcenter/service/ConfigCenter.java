package com.rookie.middleware.gateway.configcenter.service;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */

public interface ConfigCenter {

    void init(String serverAddr, String env);

    void subscribeRulesChange(RulesChangeListener listener);
}
