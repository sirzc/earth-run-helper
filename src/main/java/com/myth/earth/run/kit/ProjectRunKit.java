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

package com.myth.earth.run.kit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 项目运行进程
 *
 * @author zhouchao
 * @date 2024-11-18 下午2:53
 */
public class ProjectRunKit {

    private static final Logger logger = Logger.getInstance(ProjectRunKit.class);

    /**
     * 获取当前project中运行的服务
     *
     * @param project 当前项目
     * @return key:pid,value:name
     */
    public static Map<Long, String> getMapRunning(@NotNull Project project) {
        // 获取当前项目的运行管理器
        ExecutionManager executionManager = ExecutionManager.getInstance(project);
        ProcessHandler[] runningProcesses = executionManager.getRunningProcesses();
        if (runningProcesses == null) {
            return Collections.emptyMap();
        }

        Map<Long, String> pidToName = Maps.newHashMapWithExpectedSize(runningProcesses.length);
        for (ProcessHandler processHandler : runningProcesses) {
            BaseOSProcessHandler handler = (BaseOSProcessHandler) processHandler;
            Process process = handler.getProcess();
            String[] lines = handler.getCommandLine().split(" ");
            if (process.isAlive()) {
                pidToName.put(process.pid(), lines[lines.length - 1]);
            }
        }
        return pidToName;
    }

    /**
     * 获取运行的进程
     *
     * @param project 项目
     * @return pid列表
     */
    public static List<Long> getListRunning(@NotNull Project project) {
        // 获取当前项目的运行管理器
        ExecutionManager executionManager = ExecutionManager.getInstance(project);
        ProcessHandler[] runningProcesses = executionManager.getRunningProcesses();
        if (runningProcesses == null) {
            return Collections.emptyList();
        }

        List<Long> pids = Lists.newArrayListWithCapacity(runningProcesses.length);
        for (ProcessHandler processHandler : runningProcesses) {
            BaseOSProcessHandler handler = (BaseOSProcessHandler) processHandler;
            Process process = handler.getProcess();
            String[] lines = handler.getCommandLine().split(" ");
            if (process.isAlive()) {
                logger.info("检测到启动应用:" + lines[lines.length - 1] + "，Pid:" + process.pid());
                pids.add(process.pid());
            }
        }
        return pids;
    }

    /**
     * 获取当前活跃的project
     *
     * @return project
     */
    @Nullable
    public static Project getActiveProject() {
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        Project activeProject = null;
        Project showingProject = null;
        for (Project project : projects) {
            if (project.isOpen()) {
                Window window = WindowManager.getInstance().suggestParentWindow(project);
                if (window != null && window.isActive()) {
                    activeProject = project;
                    break;
                }
                if (window != null && window.isShowing())
                    showingProject = project;
            }
        }

        if (activeProject == null) {
            activeProject = showingProject;
        }
        return activeProject;
    }
}
