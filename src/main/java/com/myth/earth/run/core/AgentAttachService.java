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

package com.myth.earth.run.core;

import com.intellij.openapi.application.ApplicationManager;
import com.sun.tools.attach.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 利用agentmain 装载至目标jvm进程
 *
 * @author zhouchao
 * @date 2024/11/13 下午7:49
 **/
public class AgentAttachService {
    private static final Logger               logger    = Logger.getLogger(AgentAttachService.class.getName());
    /**
     * 获取jvm 列表
     */
    private final        Map<String, JvmItem> attachMap = new HashMap<>();
    private static final String               pid;

    static {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        pid = runtimeMXBean.getName().split("@")[0];
    }

    public static AgentAttachService getInstance() {
        return ApplicationManager.getApplication().getService(AgentAttachService.class);
    }

    public Collection<JvmItem> jvmList() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        // 排除自身，在执行的应用
        Map<String, JvmItem> items = list.stream()
                                         .filter(v -> !v.id().equals(pid))
                                         .map(v -> attachMap.containsKey(v.id()) ? attachMap.get(v.id()) : new JvmItem(v.id(), v.displayName()))
                                         .collect(Collectors.toMap(s -> s.id, v -> v));
        checkAndCleanJvm(items);
        return items.values();
    }

    /**
     * 获取url地址
     *
     * @param pid 进程id
     * @return url地址
     */
    @Nullable
    public String getAddressUrl(@Nullable String pid) {
        if (pid == null) {
            return null;
        }

        JvmItem jvmItem = attachMap.get(pid);
        if (jvmItem == null) {
            return null;
        }

        // 检查当前pid对应的服务是否还存在
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor descriptor : list) {
            if (descriptor.id().equals(pid)) {
                return String.format("http://%s:%s/jz", jvmItem.getTargetIp(), jvmItem.getTargetPort());
            }
        }
        return null;
    }

    /**
     * 判断目标虚拟机是否在线，不在线直接删除
     *
     * @param currentJvmMaps 当前jvm map信息
     */
    private void checkAndCleanJvm(Map<String, JvmItem> currentJvmMaps) {
        Iterator<Map.Entry<String, JvmItem>> iterator = attachMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JvmItem> next = iterator.next();
            if (!currentJvmMaps.containsKey(next.getKey())) {
                logger.info("目标jvm离线：" + next.getValue());
                iterator.remove();
            }
        }
    }

    /**
     * configs 不允许换行，使用(,)逗号分割
     *
     * @return 挂载进去的jvm信息
     */
    public JvmItem attach(@NotNull String id, String agentPath, String configs) {
        // 参数配置
        Properties configsPro = new Properties();
        if (configs != null) {
            try {
                configsPro.load(new ByteArrayInputStream(configs.replaceAll(",", "\n").getBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (!configsPro.contains("port")) {
            configsPro.put("port", getInitPort());
        }
        configs = configsPro.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(","));
        float currentJvmVersion = 0f, targetJvmVersion = 0f;
        VirtualMachine vm;
        VirtualMachineDescriptor virtualMachineDescriptor;
        String httpPort;
        String warningMessage = null;
        virtualMachineDescriptor = VirtualMachine.list().stream().filter(v -> v.id().equals(id)).findFirst().get();
        // 1.attach
        try {
            vm = VirtualMachine.attach(virtualMachineDescriptor);
            Properties targetVmProperties = vm.getSystemProperties();
            // 验证jvm版本信息
            currentJvmVersion = getJavaVersion(System.getProperties());
            targetJvmVersion = getJavaVersion(targetVmProperties);
            if (targetJvmVersion != currentJvmVersion) {
                warningMessage = String.format("与目标JVM版本不一至，可能引发agent装载错误，当前JVM%s,目标JVM%s", currentJvmVersion, targetJvmVersion);
                logger.warning(warningMessage);
            }
        } catch (AttachNotSupportedException e) {
            throw new IllegalStateException("目标虚拟机不支持Attach", e);
        } catch (IOException e) {
            throw new IllegalStateException("连接(attach)目标虚拟机失败", e);
        }
        // 2.loadAgent。然后开始执行：org.coderead.jcat.BootstrapAgent.agentmain
        try {
            vm.loadAgent(agentPath, configs);
        } catch (AgentLoadException e) {
            if ("0".equals(e.getMessage())) {
                // https://stackoverflow.com/a/54454418
                warningMessage = String.format("与目标JVM版本不一至,当前JVM%s 目标JVM%s", currentJvmVersion, targetJvmVersion);
                logger.log(Level.WARNING, warningMessage, e);
            } else {
                throw new IllegalStateException("agent装载失败", e);
            }
        } catch (AgentInitializationException e) {
            throw new IllegalStateException("agent初始化失败", e);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("Non-numeric value found")) {
                warningMessage = String.format("与目标JVM版本不一至,当前JVM%s 目标JVM%s", currentJvmVersion, targetJvmVersion);
                logger.log(Level.WARNING, warningMessage, e);
            } else {
                throw new IllegalStateException("读取目标虚拟机信息失败", e);
            }
        }
        // 3.验证端口是否顺利打开
        try {
            httpPort = vm.getSystemProperties().getProperty("earth.agent.httpPort");
            // Assert.hasText(httpPort, "agent打开端口失败：未能获取到目标通信端口");
            vm.detach();// 分离端口
        } catch (IOException e) {
            throw new IllegalStateException("读取目标虚拟机信息失败", e);
        }

        // 4.封装返回结果
        JvmItem jvmItem = new JvmItem(vm.id(), virtualMachineDescriptor.displayName());
        jvmItem.targetPort = Integer.parseInt(httpPort);
        jvmItem.attachTime = System.currentTimeMillis();
        jvmItem.jvmVersion = targetJvmVersion;
        jvmItem.warningMessage = warningMessage;
        attachMap.put(jvmItem.id, jvmItem);
        return jvmItem;
    }

    private float getJavaVersion(Properties systemProperties) {
        return Float.parseFloat(systemProperties.getProperty("java.specification.version"));
    }

    private int getInitPort() {
        return attachMap.values().stream().mapToInt(s -> s.targetPort).max().orElse(1030);
    }

    @Getter
    public static class JvmItem implements Serializable {
        String id;
        String name;
        long   attachTime; // 负载时间
        int    targetPort;// 目标虚拟机通信端口
        String targetIp = "127.0.0.1";
        String warningMessage;
        float  jvmVersion;

        public JvmItem(String id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "JvmItem{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", attachTime=" + attachTime + ", targetPort=" + targetPort + ", targetIp='"
                    + targetIp + '\'' + '}';
        }
    }
}
