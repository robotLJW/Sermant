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

package com.huawei.registry.declarers.cloud3.x;

import com.huawei.registry.interceptors.cloud3.x.ZookeeperInstanceSupplierInterceptor;
import com.huawei.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import com.huawei.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import com.huawei.sermant.core.plugin.agent.matcher.ClassMatcher;
import com.huawei.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 针对springCloud 3.x获取实例列表兼容
 * <p>
 * 自从3.x版本，zookeeper注册发现新增InstanceSupplier进行实例获取， 其将包裹原来的DiscoveryClient，因此原来的拦截点将不再受用
 * </p>
 *
 * @author zhouss
 * @since 2022-03-29
 */
public class ZookeeperInstanceSupplierDeclarer extends AbstractPluginDeclarer {
    /**
     * 增强类的全限定名
     */
    private static final String ENHANCE_CLASS =
        "org.springframework.cloud.zookeeper.discovery.ZookeeperServiceInstanceListSupplier";

    /**
     * 拦截类的全限定名
     */
    private static final String INTERCEPT_CLASS = ZookeeperInstanceSupplierInterceptor.class.getCanonicalName();

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameEquals(ENHANCE_CLASS);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
            InterceptDeclarer.build(MethodMatcher.nameEquals("filteredByZookeeperStatusUp"), INTERCEPT_CLASS)
        };
    }
}
