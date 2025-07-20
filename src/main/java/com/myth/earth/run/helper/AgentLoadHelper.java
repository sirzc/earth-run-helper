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

package com.myth.earth.run.helper;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * agent jar 加载
 *
 * @author zhouchao
 * @date 2024-11-06 上午8:40
 */
public class AgentLoadHelper {
    private static final Logger logger        = Logger.getInstance(AgentLoadHelper.class);
    private static final String LOG_AGENT_JAR = "earth-run-agent.jar";

    /**
     * 加载agent后的路径
     *
     * @return agent绝对路径
     */
    @Nullable
    public static String loadAgent(boolean refresh) {
        // 获取用户目录
        String userHomeDir = System.getProperty("user.home");
        // 构建目标目录路径
        File targetDir = new File(userHomeDir, ".jz-zhou");
        // 如果目标目录不存在，则创建它
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            logger.info("Failed to create target directory: " + targetDir.getAbsolutePath());
            return null;
        }

        // 构建目标文件路径
        File targetFile = new File(targetDir, LOG_AGENT_JAR);
        // 检查目标文件是否已经存在
        if (!refresh && targetFile.exists()) {
            logger.info("File already exists at: " + targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        }

        // 获取资源文件的输入流
        InputStream resourceInputStream = AgentLoadHelper.class.getResourceAsStream("/lib/" + LOG_AGENT_JAR);
        if (resourceInputStream == null) {
            logger.info("Resource not found: /lib/earth-run-agent.jar");
            return null;
        }

        // 复制文件
        try (BufferedInputStream bis = new BufferedInputStream(resourceInputStream);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetFile))) {
            byte[] buffer = new byte[1024];
            int length;
            // 读取输入流并写入输出流
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            logger.info("File copied successfully to: " + targetFile.getAbsolutePath());
            return targetDir.getAbsolutePath();
        } catch (IOException e) {
            logger.warn("agent load field.", e);
            return null;
        }
    }

}
