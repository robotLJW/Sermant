/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huawei.sermant.stresstest.redis.redisson.config;

import com.huawei.sermant.stresstest.core.Reflection;
import com.huawei.sermant.stresstest.redis.RedisUtils;

import java.util.Optional;

/**
 * 生产影子 single config
 *
 * @author yiwei
 * @since 2021-11-04
 */
public class ShadowMasterSlaveConfig extends ShadowConfig {
    /**
     * 构造方法
     */
    public ShadowMasterSlaveConfig() {
        super("masterSlaveServersConfig", "masterAddress");
    }

    @Override
    protected Object getAddress() {
        return RedisUtils.getMasterAddress();
    }

    @Override
    protected Optional<Object> update(Object shadowObject) {
        return super.update(shadowObject).map(configValue -> {
            Reflection.setDeclaredValue("slaveAddresses", configValue, RedisUtils.getSalveRedisAddress());
            return configValue;
        });
    }
}
