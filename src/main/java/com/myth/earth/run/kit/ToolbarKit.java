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

import com.intellij.openapi.actionSystem.*;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * tool bar 创建工具类
 *
 * @author zhouchao
 * @date 2024/5/25 下午6:57
 **/
public class ToolbarKit {

    /**
     * 创建 toolbar
     *
     * @param component     toolbar 所在的 panel
     * @param groupId    toolbar 的 action group id
     * @param horizontal 是否水平：true 水平，false 垂直
     * @return actionToolbar
     */
    public static JComponent createActionToolbar(JComponent component, String groupId, boolean horizontal) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(groupId);
        ActionGroup actionGroup = action instanceof ActionGroup ? ((ActionGroup) action) : new DefaultActionGroup();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, horizontal);
        actionToolbar.setTargetComponent(component);
        return actionToolbar.getComponent();
    }

    /**
     * 创建带toolbar的panel
     *
     * @param centerPanel 中心 panel
     * @param groupId     toolbar 的 action group id
     * @param horizontal  是否水平：true 水平，false 垂直
     * @return 带toolbar的panel
     */
    public static JComponent createPanelWithToolbar(JComponent centerPanel, String groupId, boolean horizontal) {
        // toolbar对应的位置（水平：top，垂直：left）
        String location = horizontal ? BorderLayout.NORTH : BorderLayout.WEST;
        JBInsets insets = horizontal ? JBUI.insetsBottom(1) : JBUI.insetsRight(1);

        JPanel panel = new JPanel(new BorderLayout());
        JComponent actionToolbar = createActionToolbar(panel, groupId, horizontal);
        actionToolbar.setBorder(new CustomLineBorder(insets));
        panel.add(actionToolbar, location);
        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    public static AnAction createAction(Icon icon, Consumer<AnActionEvent> consumer) {
        return new AnAction(icon) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                consumer.accept(e);
            }
        };
    }
}
