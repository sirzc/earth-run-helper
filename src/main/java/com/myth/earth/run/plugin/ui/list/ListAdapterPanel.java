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

package com.myth.earth.run.plugin.ui.list;

import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.plugin.ui.list.node.AbstractListNode;
import com.myth.earth.run.plugin.ui.list.renderer.EarthColoredListCellRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 通用列表显示
 *
 * @author zhouchao
 * @date 2024-11-27 下午4:46
 */
public class ListAdapterPanel<T extends AbstractListNode> extends JBList<T> {

    public ListAdapterPanel(@NotNull CollectionListModel<T> listModel) {
        super(listModel);
        this.setCellRenderer(new EarthColoredListCellRenderer());
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setBorder(JBUI.Borders.empty());
    }
}
