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

package com.myth.earth.run.plugin.ui.tree.node;

import com.intellij.util.ui.UIUtil;
import com.myth.earth.run.plugin.ui.tree.renderer.ObjectItemTreeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 抽象object item节点
 *
 * @author zhouchao
 * @date 2024/11/26 下午6:30
 **/
public abstract class AbstractObjectItemNode extends DefaultMutableTreeNode {

    /**
     * 渲染内容，由节点决定
     *
     * @param sonarTreeCellRenderer 渲染器
     */
    public abstract void render(ObjectItemTreeRenderer sonarTreeCellRenderer);

    /**
     * 美化空格信息展示
     *
     * @return 添加合适的空格
     */
    @NotNull
    protected static String spaceAndThinSpace() {
        String thinSpace = UIUtil.getLabelFont().canDisplay(' ') ? String.valueOf(' ') : " ";
        return " " + thinSpace;
    }
}