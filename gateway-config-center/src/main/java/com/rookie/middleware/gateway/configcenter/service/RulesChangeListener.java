package com.rookie.middleware.gateway.configcenter.service;

import com.rookie.middleware.gateway.common.rule.Rule;

import java.util.List;

/**
 * @author eumenides
 * @description
 * @date 2024/4/27
 */
public interface RulesChangeListener {
    void onRulesChange(List<Rule> rules);
}
