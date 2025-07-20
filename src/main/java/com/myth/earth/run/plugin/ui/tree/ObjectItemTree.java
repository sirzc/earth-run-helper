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

package com.myth.earth.run.plugin.ui.tree;

import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.EditSourceOnEnterKeyHandler;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.plugin.ui.tree.node.ObjectItemNode;
import com.myth.earth.run.plugin.ui.tree.renderer.ObjectItemTreeRenderer;
import org.jetbrains.annotations.NonNls;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class ObjectItemTree extends Tree implements DataProvider {

    private final Project project;

    public ObjectItemTree(Project project, TreeModel model) {
        super(model);
        this.setBorder(JBUI.Borders.empty());
        this.project = project;
        init();
    }

    private void init() {
        setShowsRootHandles(true);
        setCellRenderer(new ObjectItemTreeRenderer());
        expandRow(0);
        EditSourceOnDoubleClickHandler.install(this);
        EditSourceOnEnterKeyHandler.install(this);
    }

    @Override
    @Nullable
    public Object getData(@NonNls String dataId) {
        if (PlatformDataKeys.TREE_EXPANDER.is(dataId)) {
            return new DefaultTreeExpander(this);
        }
        return null;
    }

    @CheckForNull
    private DefaultMutableTreeNode getSelectedNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }
        return (DefaultMutableTreeNode) path.getLastPathComponent();
    }

    /**
     * 获取当前选中的节点
     *
     * @return selectedNode
     */
    @CheckForNull
    public ObjectItemNode getSelectItem() {
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        if (selectedNode instanceof ObjectItemNode) {
            return (ObjectItemNode) selectedNode;
        }
        return null;
    }
}