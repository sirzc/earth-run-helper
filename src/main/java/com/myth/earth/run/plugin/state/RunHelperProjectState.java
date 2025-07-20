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

package com.myth.earth.run.plugin.state;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.myth.earth.run.bean.GroovyCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author zhouchao
 * @date 2024-11-27 下午7:51
 */
@State(name = "com.myth.earth.run.plugin.state.RunHelperProjectState", storages = {@Storage("EarthRunHelper-setting.xml")})
public class RunHelperProjectState implements PersistentStateComponent<RunHelperProjectState> {

    public List<GroovyCode> groovyCodes;

    public static RunHelperProjectState getInstance(@NotNull Project project) {
        return project.getService(RunHelperProjectState.class);
    }

    @Override
    public @Nullable RunHelperProjectState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RunHelperProjectState restfulHelperProjectState) {
        XmlSerializerUtil.copyBean(restfulHelperProjectState, this);
    }
}
