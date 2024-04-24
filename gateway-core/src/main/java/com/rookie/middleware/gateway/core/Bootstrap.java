package com.rookie.middleware.gateway.core;

import com.rookie.middleware.gateway.core.config.Config;
import com.rookie.middleware.gateway.core.config.ConfigLoader;

/**
 * @author eumenides
 * @description API网关启动类
 * @date 2024/4/13
 */
public class Bootstrap {
    public static void main(String[] args) {
        // 加载网关核心静态配置
        Config config = ConfigLoader.getInstance().load(args);
        System.out.println(config.getPort());
        // 插件初始化
        // 初始化配置中心，链接配置中心，监听配置的增删改查
        // 启动容器
        // 加载注册中心
    }

}
