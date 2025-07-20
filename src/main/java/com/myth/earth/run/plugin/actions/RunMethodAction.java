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

package com.myth.earth.run.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.myth.earth.run.bean.EnvInfo;
import com.myth.earth.run.common.ProjectConst;
import com.myth.earth.run.kit.PsiKit;
import com.myth.earth.run.plugin.notify.PluginNotify;
import com.myth.earth.run.plugin.service.DebugUltraService;
import com.myth.earth.run.plugin.toolwindow.DebugUltraToolwindow;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * 运行选中的方法
 *
 * @author zhouchao
 * @date 2024-11-17 下午4:07
 */
public class RunMethodAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (Objects.isNull(editor) || Objects.isNull(project) || Objects.isNull(psiFile) || DumbService.isDumb(project)) {
            return;
        }

        // 判断是否支持该方法
        PsiMethod targetMethod = PsiKit.getTargetMethod(editor, psiFile);
        if (targetMethod == null) {
            return;
        }

        if (!PsiKit.hasModifierProperty(targetMethod, PsiModifier.PUBLIC)) {
            PluginNotify.info(project, "非Public不能直接调用！");
            return;
        }

        PsiClass targetClass = PsiKit.getTargetClass(editor, psiFile);
        if (targetClass == null) {
            return;
        }

        // 引入import信息
        StringJoiner headerJoiner = new StringJoiner("\n");
        headerJoiner.add("import " + targetClass.getQualifiedName());

        // 参数信息
        StringJoiner paramsJoiner = new StringJoiner("\n");
        PsiParameterList parameterList = targetMethod.getParameterList();

        // 格式信息
        StringJoiner functionJoiner = new StringJoiner(",", "(", ")");
        int parametersCount = parameterList.getParametersCount();
        if (parametersCount > 0) {
            for (PsiParameter parameter : parameterList.getParameters()) {
                PsiType psiType = parameter.getType();
                String presentableText = psiType.getPresentableText();
                paramsJoiner.add(presentableText + " " + parameter.getName() + " = " + "null");
                // 非基础数据类型，如果是对象，引入import
                if (!ProjectConst.WRAPPER_DATA_TYPE.contains(presentableText)) {
                    if (psiType instanceof PsiClassType) {
                        PsiClass psiClass = ((PsiClassType) psiType).resolve();
                        if (psiClass != null) {
                            headerJoiner.add("import " + psiClass.getQualifiedName());
                        }
                    }
                }
                functionJoiner.add(parameter.getName());
            }
        }

        String className = Optional.ofNullable(targetClass.getName()).orElse("");
        String lowerName = StringUtil.wordsToBeginFromLowerCase(className);
        String getBean = className + " " + lowerName + " = getObject(" + className + ")";
        String runMethod = lowerName + "." + targetMethod.getName() + functionJoiner;
        String params = paramsJoiner.toString();
        if (StringUtil.isNotEmpty(params)) {
            params = "\n" + params + "\n";
        }
        String code = headerJoiner + "\n" + params + "\n" + getBean + "\n" + runMethod;

        DebugUltraService debugUltraService = DebugUltraService.getInstance(project);
        debugUltraService.refreshGroovyCode(code);

        DebugUltraToolwindow.showWindow(project, ()->{
            EnvInfo envInfo = debugUltraService.getEnvInfo();
            if (envInfo.isActive()) {
                debugUltraService.refreshGroovyConsole();
            }
        });
    }
}
