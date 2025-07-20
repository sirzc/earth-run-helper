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

package com.myth.earth.run.plugin.ui.panel;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.bean.GroovyCode;
import com.myth.earth.run.common.ProjectConst;
import com.myth.earth.run.plugin.notify.PluginNotify;
import com.myth.earth.run.plugin.service.DebugUltraService;
import com.myth.earth.run.plugin.state.RunHelperProjectState;
import com.myth.earth.run.plugin.ui.list.ListAdapterPanel;
import com.myth.earth.run.plugin.ui.list.node.GroovyCodeNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * groovy code 显示面板
 *
 * @author zhouchao
 * @date 2024-11-27 下午5:43
 */
public class GroovyCodePanel extends JPanel {
    private final Project                             project;
    private final CollectionListModel<GroovyCodeNode> listModel;
    private final ListAdapterPanel<GroovyCodeNode>    listPanel;

    public GroovyCodePanel(@NotNull Project project) {
        super(new BorderLayout());
        this.project = project;
        this.listModel = new CollectionListModel<>();
        this.listPanel = new ListAdapterPanel<>(listModel);
        this.listPanel.setBorder(JBUI.Borders.empty(5));
        // 添加内容到面板中
        this.setBorder(JBUI.Borders.empty());
        this.add(createLiatToolbarPanel(), BorderLayout.CENTER);
        this.initListener();
        ProjectConst.DEFAULT_SHELL.forEach(n -> listModel.add(new GroovyCodeNode(n)));
        List<GroovyCode> groovyCodes = RunHelperProjectState.getInstance(project).groovyCodes;
        if (groovyCodes != null) {
            groovyCodes.forEach(n -> listModel.add(new GroovyCodeNode(n)));
        }
    }

    private void initListener() {
        listPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    return;
                }
                // 获取点击位置的索引
                int index = listPanel.locationToIndex(e.getPoint());
                if (index == -1) {
                    return;
                }
                GroovyCodeNode groovyCodeNode = listModel.getElementAt(index);
                DebugUltraService.getInstance(project).refreshGroovyCode(groovyCodeNode.getGroovyCode().getCode());
            }
        });
        // 添加内容变更监听器
        listPanel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                   refreshProjectGroovyCodes();
                }
            }
        });
    }

    private void refreshProjectGroovyCodes() {
        List<GroovyCodeNode> groovyCodeNodes = listModel.getItems();
        List<GroovyCode> targets = groovyCodeNodes.stream().map(GroovyCodeNode::getGroovyCode).filter(n -> !n.getDefaultFlag()).collect(Collectors.toList());
        RunHelperProjectState runHelperProjectState = RunHelperProjectState.getInstance(project);
        runHelperProjectState.groovyCodes = targets;
    }

    private JPanel createLiatToolbarPanel() {
        ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(listPanel);
        // 删除操作
        toolbarDecorator.setRemoveAction(anActionButton -> {
            int selectedIndex = listPanel.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }
            // 获取选中的节点
            GroovyCodeNode groovyCodeNode = listModel.getElementAt(selectedIndex);
            if (groovyCodeNode.getGroovyCode().getDefaultFlag()) {
                listPanel.clearSelection();
                PluginNotify.warn(project, "默认节点不可删除！");
                return;
            }
            listModel.remove(selectedIndex);
            refreshProjectGroovyCodes();
        });
        // 重置操作
        toolbarDecorator.addExtraAction(new AnActionButton("重置面板内容", AllIcons.Actions.Rollback) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
                listModel.removeAll();
                ProjectConst.DEFAULT_SHELL.forEach(n -> listModel.add(new GroovyCodeNode(n)));
                refreshProjectGroovyCodes();
            }
        });
        JPanel toolbarDecoratorPanel = toolbarDecorator.createPanel();
        toolbarDecoratorPanel.setBorder(JBUI.Borders.empty());
        toolbarDecorator.getActionsPanel().setPreferredSize(new Dimension(-1, 31));
        return toolbarDecoratorPanel;
    }

    public void saveGroovyCode(@NotNull String title, @NotNull String code) {
        GroovyCode groovyCode = new GroovyCode();
        groovyCode.setTitle(title);
        groovyCode.setCode(code);
        groovyCode.setDefaultFlag(false);
        this.listModel.add(new GroovyCodeNode(groovyCode));
        this.refreshProjectGroovyCodes();
    }
}
