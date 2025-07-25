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

package com.myth.earth.run.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.myth.earth.run.helper.AgentLoadHelper;
import org.jetbrains.annotations.NotNull;

/**
 * 插件更新通知
 *
 * @author zhouchao
 * @date 2024-04-03 16:24
 */
public class PluginUpdateNotification  implements StartupActivity {

    @Override
    public void runActivity(@NotNull Project project) {
        // 非单元测试模块
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            return;
        }
        AgentLoadHelper.loadAgent(true);
    }
}