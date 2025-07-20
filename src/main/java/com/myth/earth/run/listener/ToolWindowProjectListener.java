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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import com.myth.earth.run.common.ProjectConst;
import com.myth.earth.run.kit.ProjectRunKit;
import com.myth.earth.run.plugin.service.DebugUltraService;
import org.jetbrains.annotations.NotNull;

/**
 * toolwindow监听
 *
 * @author zhouchao
 * @date 2024-11-23 下午10:38
 */
public class ToolWindowProjectListener implements ToolWindowManagerListener {

    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        if (!ProjectConst.DEBUG_ULTRA.equals(toolWindow.getId())) {
            return;
        }

        Project activeProject = ProjectRunKit.getActiveProject();
        if (activeProject == null) {
            return;
        }

        DebugUltraService debugUltraService = DebugUltraService.getInstance(activeProject);
        debugUltraService.refreshGroovyConsole();
    }
}
