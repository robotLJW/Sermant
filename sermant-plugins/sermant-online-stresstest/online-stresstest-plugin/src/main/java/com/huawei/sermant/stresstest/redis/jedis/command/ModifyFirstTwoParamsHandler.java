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

package com.huawei.sermant.stresstest.redis.jedis.command;

/**
 * 不做任何处理的handler。
 *
 * @author yiwei
 * @since 2021-11-01
 */
public class ModifyFirstTwoParamsHandler implements Handler {
    private static final int END = 2;

    @Override
    public byte[][] handle(byte[][] bytes) {
        return addShadowPrefix(0, END, 1, bytes);
    }
}