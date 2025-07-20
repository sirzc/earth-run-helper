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

import com.intellij.icons.AllIcons;
import com.myth.earth.run.bean.GroovyCode;
import com.myth.earth.run.plugin.ui.list.renderer.EarthColoredListCellRenderer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

/**
 * Groovy shell 节点信息
 *
 * @author zhouchao
 * @date 2024-11-27 下午4:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroovyCodeNode extends AbstractListNode {

    private final GroovyCode groovyCode;

    public GroovyCodeNode(@NotNull GroovyCode groovyCode) {
        this.groovyCode = groovyCode;
    }

    @Override
    public void renderer(EarthColoredListCellRenderer coloredListCellRenderer, boolean selected) {
        if (groovyCode.getDefaultFlag()) {
            coloredListCellRenderer.setIconToolTip("默认");
            coloredListCellRenderer.setIcon(AllIcons.General.TodoDefault);
        } else {
            coloredListCellRenderer.setIconToolTip("自定义");
            coloredListCellRenderer.setIcon(AllIcons.Nodes.Favorite);
        }
        coloredListCellRenderer.append(groovyCode.getTitle());
    }
}
