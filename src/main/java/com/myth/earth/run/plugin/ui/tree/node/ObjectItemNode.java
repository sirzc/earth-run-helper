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

import com.intellij.ui.SimpleTextAttributes;
import com.myth.earth.run.bean.ObjectItem;
import com.myth.earth.run.plugin.ui.tree.renderer.ObjectItemTreeRenderer;
import com.myth.earth.run.utils.TextIconUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * object item 显示内容
 *
 * @author zhouchao
 * @date 2024-11-26 下午6:46
 */
public class ObjectItemNode extends AbstractObjectItemNode {

    private final static String EMPTY_TEXT = "No result to display";

    private final static SimpleTextAttributes RED    = SimpleTextAttributes.ERROR_ATTRIBUTES;
    private final static SimpleTextAttributes GRAY   = SimpleTextAttributes.GRAY_ATTRIBUTES;
    private final static SimpleTextAttributes GREEN  = new SimpleTextAttributes(0, TextIconUtil.NORMAL_COLOR);
    private final static SimpleTextAttributes YELLOW = new SimpleTextAttributes(0, TextIconUtil.WARNING_COLOR);

    @Setter
    @Getter
    private ObjectItem objectItem;

    public ObjectItemNode() {
    }

    public ObjectItemNode(ObjectItem objectItem) {
        this.objectItem = objectItem;
    }

    @Override
    public void render(ObjectItemTreeRenderer objectItemTreeRenderer) {
        if (objectItem == null) {
            objectItemTreeRenderer.append(EMPTY_TEXT);
            return;
        }

        String flag = objectItem.getFlag();
        if (flag == null) {
            if (objectItem.isAtomic()) {
                objectItemTreeRenderer.append(objectItem.getName() + " = " + objectItem.getValue(), GREEN);
            } else {
                String baseInfo = String.format("%s@%s", getSmallType(objectItem.getType()), objectItem.getObjectId());
                objectItemTreeRenderer.append(objectItem.getName());
                objectItemTreeRenderer.append(spaceAndThinSpace() + "=" + spaceAndThinSpace(), RED);
                objectItemTreeRenderer.append(baseInfo);
            }
            return;
        }

        // objectItemTreeRenderer.append(flag + spaceAndThinSpace(), RED);
        switch (flag) {
            case "root":
                rootRenderer(objectItemTreeRenderer, objectItem);
                break;
            case "property":
                propertyRenderer(objectItemTreeRenderer, objectItem);
                break;
            case "method":
                methodRenderer(objectItemTreeRenderer, objectItem);
                break;
            case "entry":
                entryRenderer(objectItemTreeRenderer, objectItem);
                break;
            case "index":
                indexRenderer(objectItemTreeRenderer, objectItem);
                break;
            default:
                break;
        }
    }

    private String getSmallType(@Nullable String type) {
        if (type == null) {
            return "";
        }
        int lastDotIndex = type.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return type.substring(lastDotIndex + 1);
        } else {
            return type;
        }
    }

    private void rootRenderer(@NotNull ObjectItemTreeRenderer renderer, @NotNull ObjectItem objectItem) {
        if (objectItem.getChildSize() != null) {
            String smallType = getSmallType(objectItem.getType());
            String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
            renderer.append(baseInfo, GRAY);
            renderer.append(spaceAndThinSpace() + "size=" + objectItem.getChildSize(), YELLOW);
            return;
        }

        if (objectItem.isAtomic()) {
            renderer.append(objectItem.getValue(), GREEN);
            return;
        }

        String smallType = getSmallType(objectItem.getType());
        String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
        renderer.append(baseInfo, GRAY);
    }

    private void indexRenderer(@NotNull ObjectItemTreeRenderer renderer, @NotNull ObjectItem objectItem) {
        if (objectItem.isAtomic()) {
            renderer.append(objectItem.getName() + " = " + objectItem.getValue(), GREEN);
        } else {
            String smallType = getSmallType(objectItem.getType());
            String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
            renderer.append(objectItem.getName());
            renderer.append(spaceAndThinSpace() + "=" + spaceAndThinSpace(), RED);
            renderer.append(baseInfo, GRAY);
        }
    }

    private void propertyRenderer(@NotNull ObjectItemTreeRenderer renderer, @NotNull ObjectItem objectItem) {
        if (objectItem.isAtomic()) {
            renderer.append(objectItem.getName() + " = " + objectItem.getValue(), GREEN);
        } else {
            String smallType = getSmallType(objectItem.getType());
            String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
            renderer.append(objectItem.getName());
            renderer.append(spaceAndThinSpace() + "=" + spaceAndThinSpace(), RED);
            renderer.append(baseInfo, GRAY);

            if (objectItem.getChildSize() != null) {
                renderer.append(spaceAndThinSpace() + "size=" + objectItem.getChildSize(), YELLOW);
            }
        }
    }

    private void entryRenderer(@NotNull ObjectItemTreeRenderer renderer, @NotNull ObjectItem objectItem) {
        if (objectItem.isAtomic()) {
            renderer.append(objectItem.getName() + " = " + objectItem.getValue(), GREEN);
        } else {
            String smallType = getSmallType(objectItem.getType());
            String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
            renderer.append(baseInfo);
            renderer.append(spaceAndThinSpace() + "\"" + objectItem.getValue() + "\"", GRAY);
        }
    }

    private void methodRenderer(@NotNull ObjectItemTreeRenderer renderer, @NotNull ObjectItem objectItem) {
        String smallType = getSmallType(objectItem.getType());
        String baseInfo = String.format("%s@%s", smallType, objectItem.getObjectId());
        renderer.append(baseInfo, GRAY);
    }
}
