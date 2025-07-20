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

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * psi 操作组件类
 *
 * @author changan
 * @date 2023-04-25 21:10
 */
public final class PsiKit {

    /**
     * 获取方法的所有注解（包括父类）
     *
     * @param psiMethod psiMethod
     * @return annotations
     */
    @NotNull
    public static List<PsiAnnotation> getMethodAnnotations(@NotNull PsiMethod psiMethod) {
        List<PsiAnnotation> annotations = new ArrayList<>(Arrays.asList(psiMethod.getModifierList().getAnnotations()));
        for (PsiMethod superMethod : psiMethod.findSuperMethods()) {
            getMethodAnnotations(superMethod)
                    .stream()
                    // 筛选：子类中方法定义了父类中方法存在的注解时只保留最上层的注解（即实现类的方法注解
                    .filter(annotation -> !annotations.contains(annotation))
                    .forEach(annotations::add);
        }
        return annotations;
    }


    /**
     * 校验字段是否有某个修饰符
     *
     * @param psiField - 字段
     * @param psiModifier - 属性
     * @return ture or false
     */
    public static boolean hasModifierProperty(@NotNull PsiField psiField, String psiModifier) {
        PsiModifierList modifierList = psiField.getModifierList();
        if (modifierList == null) {
            return false;
        }
        return modifierList.hasModifierProperty(psiModifier);
    }


    /**
     * 判断方法是否是static、public等修饰符
     * @param psiMethod 方法
     * @param psiModifier 属性
     * @return ture or false
     */
    public static boolean hasModifierProperty(@NotNull PsiMethod psiMethod, @NotNull String psiModifier) {
        PsiModifierList modifierList = psiMethod.getModifierList();
        return modifierList.hasModifierProperty(psiModifier);
    }

    /**
     * 获取目标类
     *
     * @param editor 编辑器
     * @param psiFile 文件信息
     * @return 目标类
     */
    @Nullable
    public static PsiClass getTargetClass(@NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (Objects.isNull(element)) {
            return null;
        }
        // 当前类
        PsiClass target = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return target instanceof SyntheticElement ? null : target;
    }

    /**
     * 从虚拟文件中获取类信息
     *
     * @param project 项目
     * @param virtualFile 虚拟文件
     * @return 虚拟文件对应的类
     */
    @Nullable
    public static PsiClass getPsiClassFromVirtualFile(@NotNull Project project,@NotNull VirtualFile virtualFile) {
        if (!virtualFile.getFileType().isBinary() && Objects.equals(virtualFile.getExtension(), "java")) {
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            if (psiFile instanceof PsiJavaFile) {
                PsiClass targetClass = PsiTreeUtil.findChildOfType(psiFile, PsiClass.class);
                // 获取Java类或者接口
                if (targetClass == null || targetClass.isAnnotationType() || targetClass.isEnum()) {
                    return null;
                }
                return targetClass;
            }
        }
        return null;
    }

    /**
     * 获取目标方法，当前游标所在行的方法
     *
     * @param editor 编辑器
     * @param psiFile 文件信息
     * @return 目标方法
     */
    @Nullable
    public static PsiMethod getTargetMethod(@NotNull Editor editor, @NotNull PsiFile psiFile) {
        int offset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(offset);
        if (Objects.isNull(element)) {
            return null;
        }
        // 当前方法
        PsiMethod target = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        return target instanceof SyntheticElement ? null : target;
    }

    /**
     * 判断一个方法是否为api方法，非静态、公开，且包含指定注解
     *
     * @param psiMethod   方法
     * @param annotations 方法注解包路径
     * @return 是否支持
     */
    public static boolean supportApiMethod(@NotNull PsiMethod psiMethod, @NotNull Collection<String> annotations) {
        if (psiMethod.isConstructor()) {
            return false;
        }
        return ApplicationManager.getApplication().runReadAction((Computable<Boolean>) () -> {
            // 非公开 或 静态方法
            if (!hasModifierProperty(psiMethod, PsiModifier.PUBLIC) || hasModifierProperty(psiMethod, PsiModifier.STATIC)) {
                return false;
            }
            // 是否包含注解
            return AnnotationUtil.isAnnotated(psiMethod, annotations, 0);
        });
    }

    /**
     * 获取指定注解的值
     *
     * @param psiParameter   参数信息
     * @param annotationPath 注解地址
     * @param property       属性
     * @return 值
     */
    public static String getPropertyFromAnnotation(@NotNull PsiParameter psiParameter, @NotNull String annotationPath, @NotNull String property) {
        Optional<PsiAnnotation> optional = Optional.ofNullable(psiParameter.getAnnotation(annotationPath));
        return optional.map(p -> p.findAttributeValue(property))
                       .map(PsiAnnotationMemberValue::getText)
                       .map(s -> s.replace("\"", ""))
                       .filter(StringUtils::isNotBlank)
                       .orElse(null);
    }
}
