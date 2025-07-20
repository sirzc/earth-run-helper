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

package com.myth.earth.run.plugin.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.myth.earth.run.plugin.service.DebugUltraService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 超级debug toolwindow
 *
 * @author zhouchao
 * @date 2024-11-21 下午5:02
 */
public class DebugUltraToolwindow implements ToolWindowFactory {

    @Override
    public void init(@NotNull ToolWindow toolWindow) {
        toolWindow.setStripeTitle("Debug Ultra");
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DebugUltraService debugUltraService = DebugUltraService.getInstance(project);
        toolWindow.getContentManager().addContent(debugUltraService.getConsoleContent());
    }

    @Nullable
    public static ToolWindow getToolWindow(@NotNull Project project) {
        return ToolWindowManager.getInstance(project).getToolWindow("EarthRunHelper.Tool");
    }

    public static void showWindow(@NotNull Project project, @Nullable Runnable onShow) {
        ToolWindow window = getToolWindow(project);
        if (window == null) {
            return;
        }
        window.show(onShow);
    }
}
