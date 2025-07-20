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

import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.bean.EnvInfo;
import com.myth.earth.run.bean.ObjectItem;
import com.myth.earth.run.helper.EarthHttpHelper;
import com.myth.earth.run.plugin.notify.PluginNotify;
import com.myth.earth.run.plugin.service.DebugUltraService;
import com.myth.earth.run.plugin.ui.tree.ObjectItemTree;
import com.myth.earth.run.plugin.ui.tree.node.ObjectItemNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * jvm run result panel
 *
 * @author zhouchao
 * @date 2024/11/26 下午6:50
 **/
public class JvmResultPanel extends JBScrollPane {
    private final Project          project;
    private final DefaultTreeModel model;
    private final ObjectItemTree   tree;
    private final ObjectItemNode   summaryNode;

    public JvmResultPanel(@NotNull Project project) {
        this.setBorder(JBUI.Borders.empty());
        this.project = project;
        this.summaryNode = new ObjectItemNode();
        this.model = new DefaultTreeModel(summaryNode);
        this.tree = new ObjectItemTree(project, model);

        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setViewportView(tree);
        new TreeSpeedSearch(tree);
        this.initTreeListener();
    }

    private void initTreeListener() {
        this.tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    return;
                }

                ObjectItemNode objectItemNode = tree.getSelectItem();
                if (objectItemNode == null || objectItemNode.getObjectItem() == null) {
                    return;
                }

                // 已存在的不再加载
                int childCount = objectItemNode.getChildCount();
                if (childCount > 0) {
                    return;
                }

                ObjectItem objectItem = objectItemNode.getObjectItem();
                if (objectItem.isAtomic()) {
                    return;
                }

                List<ObjectItem> children = objectItem.getChildren();
                if (children != null && !children.isEmpty()) {
                    refreshChildNode(objectItemNode, children);
                    return;
                }

                EnvInfo envInfo = DebugUltraService.getInstance(project).getEnvInfo(false);
                if (envInfo.isActive()) {
                    List<ObjectItem> objectItems = EarthHttpHelper.detail(envInfo.getAddressUrl(), envInfo.getSessionId(), objectItem);
                    refreshChildNode(objectItemNode, objectItems);
                } else {
                    PluginNotify.warn(project, "已断开链接，无法查看！");
                }
            }
        });
    }

    public void refreshJvmResultTree(@Nullable ObjectItem objectItem) {
        this.summaryNode.setObjectItem(objectItem);
        this.summaryNode.removeAllChildren();
        // 节点结构变更，不然清除后依旧能看到
        this.model.nodeStructureChanged(summaryNode);
        this.tree.expandRow(0);
    }

    public void refreshChildNode(@NotNull ObjectItemNode parentNode, @NotNull List<ObjectItem> objectItems) {
        // 渲染子节点信息
        for (int i = 0; i < objectItems.size(); i++) {
            ObjectItem objectItem = objectItems.get(i);
            parentNode.insert(new ObjectItemNode(objectItem), i);
            int[] newIdx = {i};
            // 节点被插入
            this.model.nodesWereInserted(parentNode, newIdx);
            // 通知节点变化
            this.model.nodeChanged(parentNode);
        }
        this.tree.expandPath(tree.getSelectionPath());
    }
}