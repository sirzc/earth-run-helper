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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.Semaphore;
import com.myth.earth.run.plugin.notify.PluginNotify;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * 后台任务助手
 *
 * @author zhouchao
 * @date 2024/11/8 上午10:37
 **/
public final class ProgressHelper {
    private static final Logger  logger            = Logger.getInstance(ProgressHelper.class);
    /**
     * 默认等待时长：1000ms
     */
    private static final Integer DEFAULT_WAIT_TIME = 1000;

    /**
     * 执行可取消的任务
     *
     * @param project  项目
     * @param title    任务标题
     * @param consumer ProgressIndicator进度指示器
     */
    public static void doCancelableTask(@NotNull Project project, @NotNull String title, @NotNull Consumer<ProgressIndicator> consumer) {
        new Task.Backgroundable(project, title, true) {
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                Semaphore done = new Semaphore();
                done.down();

                ApplicationManager.getApplication().executeOnPooledThread(() -> {
                    try {
                        consumer.accept(progressIndicator);
                    } catch (Exception e) {
                        PluginNotify.warn(project, "Background Task [" + title + "] execute error:" + e.getMessage());
                        logger.warn("Background Task [" + title + "] execute error", e);
                    } finally {
                        done.up();
                    }
                });

                while (!done.waitFor(DEFAULT_WAIT_TIME)) {
                    if (progressIndicator.isCanceled()) {
                        break;
                    }
                }
            }
        }.queue();
    }
}
