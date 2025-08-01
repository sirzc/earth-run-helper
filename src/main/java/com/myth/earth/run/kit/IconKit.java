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

import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author zhouchao
 * @date 2024-11-22 下午11:06
 */
public interface IconKit {

    @NotNull
    Icon SUCCESS = IconManager.getInstance().getIcon("icons/green@jz.svg", IconKit.class);

    @NotNull
    Icon FAIL = IconManager.getInstance().getIcon("icons/red@jz.svg", IconKit.class);
}
