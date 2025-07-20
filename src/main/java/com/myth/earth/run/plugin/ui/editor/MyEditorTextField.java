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

package com.myth.earth.run.plugin.ui.editor;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ui.EditorTextField;
import com.intellij.util.LocalTimeCounter;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.common.ProjectConst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.groovy.GroovyFileType;

import javax.swing.border.Border;

/**
 * text field editor <br/>
 * <p>
 * fileType 示例：
 *
 * @author zhouchao
 * @date 2024/6/17 下午4:58
 * @see FileTypes#PLAIN_TEXT
 * @see com.intellij.json.JsonFileType#INSTANCE
 * @see com.intellij.ide.highlighter.HtmlFileType#INSTANCE
 * @see com.intellij.ide.highlighter.XmlFileType#INSTANCE
 **/
public class MyEditorTextField extends EditorTextField {

    public MyEditorTextField(@NotNull Project project) {
        this(project, GroovyFileType.GROOVY_FILE_TYPE);
    }

    public MyEditorTextField(@NotNull Project project, @NotNull FileType fileType) {
        // 非视图、非行模型
        super(null, project, fileType, false, false);
    }

    public static void setupTextFieldEditor(@NotNull EditorEx editor) {
        // 获取编辑器的设置对象。
        EditorSettings settings = editor.getSettings();
        // 启用代码折叠轮廓显示，便于隐藏和展开代码块。
        settings.setFoldingOutlineShown(true);
        // 启用行号显示，帮助定位代码行。
        settings.setLineNumbersShown(true);
        // 启用缩进引导线显示，辅助显示代码的缩进结构。
        settings.setIndentGuidesShown(true);
        // 使水平滚动条可见，以应对宽度过大的代码行。
        editor.setHorizontalScrollbarVisible(true);
        // 使垂直滚动条可见，以应对过多的代码行。
        editor.setVerticalScrollbarVisible(true);
    }

    public void setText(@Nullable final String text, @NotNull final FileType fileType) {
        super.setFileType(fileType);
        Document document = createDocument(text, fileType);
        setDocument(document);
        PsiFile psiFile = PsiDocumentManager.getInstance(getProject()).getPsiFile(document);
        if (psiFile != null) {
            try {
                WriteCommandAction.runWriteCommandAction(getProject(), () -> {
                    CodeStyleManager.getInstance(getProject()).reformat(psiFile);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setFileType(@NotNull FileType fileType) {
        setNewDocumentAndFileType(fileType, createDocument(getText(), fileType));
    }

    @Override
    protected Document createDocument() {
        return createDocument(null, getFileType());
    }

    private void initOneLineModePre(@NotNull final EditorEx editor) {
        editor.setOneLineMode(false);
        editor.setColorsScheme(editor.createBoundColorSchemeDelegate(null));
        editor.getSettings().setCaretRowShown(false);
    }

    @NotNull
    @Override
    protected EditorEx createEditor() {
        EditorEx editor = super.createEditor();
        initOneLineModePre(editor);
        setupTextFieldEditor(editor);
        return editor;
    }

    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        super.repaint(tm, x, y, width, height);
        if (getEditor() instanceof EditorEx) {
            initOneLineModePre(((EditorEx) getEditor()));
        }
    }

    @Override
    public void setBorder(Border border) {
        super.setBorder(JBUI.Borders.empty());
    }

    public Document createDocument(@Nullable final String text, @NotNull final FileType fileType) {
        final PsiFileFactory factory = PsiFileFactory.getInstance(getProject());
        final long stamp = LocalTimeCounter.currentTime();
        final PsiFile psiFile = factory.createFileFromText(ProjectConst.PROJECT_OUTPUT_DIRECTORY, fileType, text == null ? "" : text, stamp, true, false);
        return PsiDocumentManager.getInstance(getProject()).getDocument(psiFile);
    }
}
