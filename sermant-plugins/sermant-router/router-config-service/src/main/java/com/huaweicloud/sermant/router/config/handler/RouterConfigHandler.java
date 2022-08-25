/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.router.config.handler;

import com.huaweicloud.sermant.core.plugin.subscribe.processor.OrderConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.label.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.label.entity.Rule;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 路由配置处理器（全集）
 *
 * @author provenceee
 * @since 2022-08-09
 */
public class RouterConfigHandler extends AbstractConfigHandler {
    @Override
    public void handle(DynamicConfigEvent event, RouterConfiguration configuration) {
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetRouteRule(Collections.emptyMap());
            RuleUtils.initHeaderKeys(configuration);
            return;
        }
        Map<String, String> routeRuleMap = getRouteRuleMap(event);
        Map<String, List<Rule>> routeRule = new HashMap<>();
        for (Entry<String, String> entry : routeRuleMap.entrySet()) {
            List<Map<String, String>> routeRuleList = yaml.load(entry.getValue());
            if (CollectionUtils.isEmpty(routeRuleList)) {
                continue;
            }
            List<Rule> list = JSONArray.parseArray(JSONObject.toJSONString(routeRuleList), Rule.class);
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (Rule rule : list) {
                // 去掉无效的规则
                RuleUtils.removeInvalidRules(rule.getMatch());

                // 去掉无效的路由
                RuleUtils.removeInvalidRoute(rule.getRoute());
            }
            list.sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
            routeRule.put(entry.getKey(), list);
        }
        configuration.resetRouteRule(routeRule);
        RuleUtils.initHeaderKeys(configuration);
    }

    private Map<String, String> getRouteRuleMap(DynamicConfigEvent event) {
        if (event instanceof OrderConfigEvent) {
            Map<String, Object> allData = ((OrderConfigEvent) event).getAllData();
            String prefix =
                RouterConstant.ROUTER_CONFIG_SERVICECOMB_KEY + "." + RouterConstant.ROUTER_CONFIG_ROUTE_RULE_KEY + ".";
            Map<String, String> routeRuleMap = new HashMap<>();
            for (Entry<String, Object> entry : allData.entrySet()) {
                if (!entry.getKey().startsWith(prefix)) {
                    continue;
                }
                Object value = entry.getValue();
                if (value instanceof String) {
                    routeRuleMap.put(entry.getKey().substring(prefix.length()), (String) value);
                } else {
                    routeRuleMap.put(entry.getKey().substring(prefix.length()), yaml.dump(value));
                }
            }
            return routeRuleMap;
        }
        String content = event.getContent();
        Map<String, Map<String, Map<String, String>>> load = yaml.load(content);
        if (CollectionUtils.isEmpty(load)) {
            return Collections.emptyMap();
        }
        Map<String, Map<String, String>> servicecomb = load.get(RouterConstant.ROUTER_CONFIG_SERVICECOMB_KEY);
        if (CollectionUtils.isEmpty(servicecomb)) {
            return Collections.emptyMap();
        }
        return servicecomb.get(RouterConstant.ROUTER_CONFIG_ROUTE_RULE_KEY);
    }
}