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

package com.myth.earth.run.plugin.ui.tree.renderer;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.myth.earth.run.plugin.ui.tree.node.AbstractObjectItemNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * object item 渲染器
 *
 * @author zhouchao
 * @date 2024/11/26 下午6:29
 **/
public class ObjectItemTreeRenderer extends ColoredTreeCellRenderer {

    private String iconToolTip = null;

    @Override
    public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        AbstractObjectItemNode node = (AbstractObjectItemNode) value;
        node.render(this);
    }

    public void setIconToolTip(String toolTip) {
        this.iconToolTip = toolTip;
    }

    /**
     * 在图标位置，显示图标的描述，在其他位置其他描述
     *
     * @param event 鼠标事件
     * @return 提示文本
     */
    @Override
    public String getToolTipText(MouseEvent event) {
        if (this.iconToolTip == null) {
            return super.getToolTipText(event);
        }
        if (event.getX() < getIconWidth()) {
            return this.iconToolTip;
        }
        return super.getToolTipText(event);
    }

    private int getIconWidth() {
        if (getIcon() != null) {
            return getIcon().getIconWidth() + this.myIconTextGap;
        }
        return 0;
    }
}
