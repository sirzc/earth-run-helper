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

package com.myth.earth.run.plugin.ui.list.node;

import com.intellij.util.ui.UIUtil;
import com.myth.earth.run.plugin.ui.list.renderer.EarthColoredListCellRenderer;
import org.jetbrains.annotations.NotNull;

/**
 * 抽象节点信息
 *
 * @author zhouchao
 * @date 2024-11-27 下午4:33
 */
public abstract class AbstractListNode {

    /**
     * 渲染
     *
     * @param coloredListCellRenderer 渲染器
     * @param selected 是否选中
     */
    public abstract void renderer(EarthColoredListCellRenderer coloredListCellRenderer, boolean selected);

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
