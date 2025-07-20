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

package com.myth.earth.run.plugin.service;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.bean.ClassloaderInfo;
import com.myth.earth.run.bean.EnvInfo;
import com.myth.earth.run.bean.ObjectItem;
import com.myth.earth.run.common.ProjectConst;
import com.myth.earth.run.core.AgentAttachService;
import com.myth.earth.run.helper.AgentLoadHelper;
import com.myth.earth.run.helper.EarthHttpHelper;
import com.myth.earth.run.helper.ProgressHelper;
import com.myth.earth.run.kit.ProjectRunKit;
import com.myth.earth.run.plugin.dialog.VirtualSelectDialog;
import com.myth.earth.run.plugin.notify.PluginNotify;
import com.myth.earth.run.plugin.ui.panel.GroovyCodePanel;
import com.myth.earth.run.plugin.ui.panel.GroovyConsolePanel;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * agent debug 服务
 *
 * @author zhouchao
 * @date 2024-11-20 下午9:58
 */
public class DebugUltraService {
    private final Project              project;
    /**
     * groovy编辑器和jvm信息展示
     */
    private final GroovyConsolePanel groovyConsolePanel;
    /**
     * groovy shell 脚本列表
     */
    private final GroovyCodePanel    groovyCodePanel;
    /**
     * 当前环境信息
     */
    private final EnvInfo            envInfo;

    public DebugUltraService(@NotNull Project project) {
        this.project = project;
        this.groovyConsolePanel = new GroovyConsolePanel(project);
        this.groovyCodePanel = new GroovyCodePanel(project);
        this.envInfo = new EnvInfo();
    }

    public static DebugUltraService getInstance(@NotNull Project project) {
        return project.getService(DebugUltraService.class);
    }

    public Content getConsoleContent() {
        OnePixelSplitter leftSplitter = new OnePixelSplitter(false, "JZ.Console.Shell", 0.25f);
        leftSplitter.setBorder(JBUI.Borders.empty());
        leftSplitter.setFirstComponent(groovyCodePanel);
        leftSplitter.setSecondComponent(groovyConsolePanel);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty());
        rootPanel.add(leftSplitter, BorderLayout.CENTER);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        return contentFactory.createContent(rootPanel, "控制台", false);
    }

    private boolean checkValidPid(@Nullable String pid) {
        if (pid == null) {
            return false;
        }

        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        if (list.isEmpty()) {
            return false;
        }

        for (VirtualMachineDescriptor descriptor : list) {
            if (descriptor.id().equals(pid)) {
                return true;
            }
        }
        return false;
    }

    private String selectRunPid() {
        List<Long> running = ProjectRunKit.getListRunning(project);
        if (running.size() == 1) {
            return String.valueOf(running.get(0));
        }

        VirtualSelectDialog virtualSelectDialog = new VirtualSelectDialog(project);
        virtualSelectDialog.setVisible(true);
        return virtualSelectDialog.getPid();
    }

    @Nullable
    private String getClassloaderId() {
        // 重新建立链接
        String classloader = groovyConsolePanel.getSelectClassloader();
        if (classloader == null) {
            return null;
        }
        List<ClassloaderInfo> classloaderInfos = envInfo.getClassloaderInfos();
        if (classloaderInfos == null) {
            return null;
        }
        for (ClassloaderInfo classloaderInfo : classloaderInfos) {
            if (classloaderInfo.getType().equals(classloader)) {
                return classloaderInfo.getId();
            }
        }
        return null;
    }

    /**
     * 获取环境信息，获取不到会重新连接
     *
     * @return 当前环境信息
     */
    @NotNull
    public EnvInfo getEnvInfo() {
        return getEnvInfo(true);
    }

    /**
     * 获取当前环境信息
     *
     * @param reconnect 重连
     * @return 获取当前环境信息
     */
    @NotNull
    public EnvInfo getEnvInfo(boolean reconnect) {
        String pid = envInfo.getPid();
        // 有效
        if (checkValidPid(pid)) {
            return envInfo;
        }

        // 重置连接信息
        envInfo.reset();

        // 不进行重连，直接返回
        if (!reconnect) {
            return envInfo;
        }

        // 无效条件下，重新选择需要挂载的进程
        pid = selectRunPid();
        if (pid == null) {
            return envInfo;
        }

        // 获取资源文件夹下的 lib 目录的 URL
        String agentPath = AgentLoadHelper.loadAgent(false);
        AgentAttachService agentAttachService = AgentAttachService.getInstance();
        AgentAttachService.JvmItem jvmItem = agentAttachService.attach(pid, agentPath, null);
        if (jvmItem != null) {
            envInfo.init(pid, String.format("http://%s:%s/jz", jvmItem.getTargetIp(), jvmItem.getTargetPort()));
        }
        return envInfo;
    }

    public void refreshGroovyConsole() {
        String pid = envInfo.getPid();
        if (!checkValidPid(pid)) {
            envInfo.reset();
            groovyConsolePanel.refreshGroovyData(null, null);
            return;
        }

        List<ClassloaderInfo> classloaderInfos = envInfo.getClassloaderInfos();
        if (classloaderInfos == null || classloaderInfos.isEmpty()) {
            classloaderInfos = EarthHttpHelper.getAllClassLoaders(envInfo.getAddressUrl());
            envInfo.setClassloaderInfos(classloaderInfos);
        }
        // 刷新下拉选项
        groovyConsolePanel.refreshGroovyData(pid, classloaderInfos);
    }

    public void refreshGroovySession() {
        String pid = envInfo.getPid();
        if (!checkValidPid(pid)) {
            envInfo.reset();
            groovyConsolePanel.refreshGroovyData(null, null);
            return;
        }

        // 清空输入内容
        groovyConsolePanel.resetGroovyCode();

        // 先断开链接，再重新建立新的链接
        if (envInfo.getSessionId() != null) {
            EarthHttpHelper.closeSession(envInfo.getAddressUrl(), envInfo.getSessionId());
        }

        // 重新建立链接
        String classloaderId = getClassloaderId();
        if (classloaderId != null) {
            envInfo.setSessionId(EarthHttpHelper.openSession(envInfo.getAddressUrl(), classloaderId));
        }
    }

    public void doProcessGroovyCode() {
        String pid = envInfo.getPid();
        if (!checkValidPid(pid)) {
            PluginNotify.warn(project, "请点击“未连接”建立连接！");
            return;
        }

        if (envInfo.getSessionId() == null) {
            String classloaderId = getClassloaderId();
            if (classloaderId == null) {
                PluginNotify.warn(project, "无法加载类加载器信息，请点击刷新按钮！");
                return;
            }
            String sessionId = EarthHttpHelper.openSession(envInfo.getAddressUrl(), classloaderId);
            envInfo.setSessionId(sessionId);
        }

        if (envInfo.getSessionId() == null) {
            PluginNotify.warn(project, "无法建立链接，请点击刷新按钮！");
            return;
        }

        String sessionId = envInfo.getSessionId();
        String groovyCode = ProjectConst.GROOVY_HEAD + groovyConsolePanel.getGroovyCode();
        System.out.println(groovyCode);
        ProgressHelper.doCancelableTask(project, sessionId, progressIndicator -> {
            ObjectItem result = EarthHttpHelper.eval(envInfo.getAddressUrl(), sessionId, groovyCode);
            ApplicationManager.getApplication().invokeLater(() -> groovyConsolePanel.refreshJvmResult(result));
        });
    }

    public void refreshGroovyCode(String groovyCode) {
        groovyConsolePanel.refreshGroovyCode(groovyCode);
    }

    public void saveGroovyCodeShell() {
        String groovyCode = groovyConsolePanel.getGroovyCode();
        if (groovyCode == null || groovyCode.trim().isEmpty()) {
            return;
        }

        String inputDialog = Messages.showInputDialog("请输入标题", "保存代码", AllIcons.Actions.MenuSaveall);
        if (inputDialog == null) {
            return;
        }

        groovyCodePanel.saveGroovyCode(inputDialog, groovyCode);
    }
}
