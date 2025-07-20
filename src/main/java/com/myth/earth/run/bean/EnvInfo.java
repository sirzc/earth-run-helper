/*
 * Copyright (c) 2025 周潮. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.myth.earth.run.bean;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 当前环境信息
 *
 * @author zhouchao
 * @date 2024-11-25 上午11:03
 */
@Data
public class EnvInfo {
    /**
     * 当前pid
     */
    private String                pid;
    /**
     * 当前地址
     */
    private String                addressUrl;
    /**
     * sessionId
     */
    private String                sessionId;
    /**
     * 类加载器信息
     */
    private List<ClassloaderInfo> classloaderInfos;
    /**
     * 是否活跃
     */
    private boolean               active = false;

    public void reset() {
        pid = null;
        addressUrl = null;
        sessionId = null;
        classloaderInfos = null;
        active = false;
    }

    public void init(@NotNull String pid,@NotNull String addressUrl) {
        this.pid = pid;
        this.addressUrl = addressUrl;
        this.active = true;
    }
}
