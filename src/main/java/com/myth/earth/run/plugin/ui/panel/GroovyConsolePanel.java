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

package com.myth.earth.run.plugin.ui.panel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;
import com.intellij.ui.OnePixelSplitter;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.bean.ClassloaderInfo;
import com.myth.earth.run.bean.EnvInfo;
import com.myth.earth.run.bean.ObjectItem;
import com.myth.earth.run.kit.IconKit;
import com.myth.earth.run.kit.ToolbarKit;
import com.myth.earth.run.plugin.service.DebugUltraService;
import com.myth.earth.run.plugin.ui.editor.MyEditorTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * groovy 操作分割面板
 *
 * @author zhouchao
 * @date 2024-11-22 下午4:13
 */
public class GroovyConsolePanel extends OnePixelSplitter {

    private final Project           project;
    /**
     * groovy editor编辑器
     */
    private final MyEditorTextField groovyEditor;
    /**
     * pid展示区域
     */
    private final JBLabel           pidLabel;
    /**
     * 类加载器选择项
     */
    private final ComboBox<String>  classloaderComboBox;
    /**
     * 执行状态信息展示
     */
    private final JBTextArea        runStatusArea;
    private final JvmResultPanel jvmResultPanel;

    public GroovyConsolePanel(final Project project) {
        super(false, "JZ.ConsoleRun", 0.5f);
        this.project = project;
        this.groovyEditor = new MyEditorTextField(project);
        this.groovyEditor.setBorder(JBUI.Borders.empty());
        this.groovyEditor.setText("// getObject(class) or get(class)\n");

        this.pidLabel = new JBLabel();
        this.pidLabel.setBorder(JBUI.Borders.empty(0, 5));
        this.pidLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        this.classloaderComboBox = new ComboBox<>();

        this.jvmResultPanel = new JvmResultPanel(project);
        this.runStatusArea = new JBTextArea();
        this.runStatusArea.setBorder(JBUI.Borders.empty(5));
        // this.runStatusArea.setFont(new Font("Arial", Font.BOLD, 14));

        // 设置左右面板信息
        this.setFirstComponent(getGroovyConsolePanel());
        this.setSecondComponent(getJvmResultInfoPanel());
        // 注册监听
        this.initCommentListener();
    }

    private void initCommentListener() {
        this.pidLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DebugUltraService debugUltraService = DebugUltraService.getInstance(project);
                EnvInfo envInfo = debugUltraService.getEnvInfo();
                if (envInfo.isActive()) {
                    debugUltraService.refreshGroovyConsole();
                }
            }
        });
    }

    private JComponent getGroovyConsolePanel() {
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setPreferredSize(new Dimension(-1, 30));
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
        toolbarPanel.setBorder(JBUI.Borders.empty());
        toolbarPanel.add(ToolbarKit.createActionToolbar(toolbarPanel, "EarthRunHelper.ConsoleRun", true));
        toolbarPanel.add(Box.createHorizontalGlue());
        toolbarPanel.add(this.pidLabel);
        toolbarPanel.add(Box.createHorizontalStrut(5));

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty());
        rootPanel.add(toolbarPanel, BorderLayout.NORTH);
        rootPanel.add(groovyEditor, BorderLayout.CENTER);
        return rootPanel;
    }

    private JComponent getJvmResultInfoPanel() {
        JPanel toolbarPanel = new JPanel();
        toolbarPanel.setPreferredSize(new Dimension(-1, 31));
        toolbarPanel.setLayout(new BoxLayout(toolbarPanel, BoxLayout.X_AXIS));
        toolbarPanel.setBorder(new CustomLineBorder(JBUI.insetsBottom(1)));
        toolbarPanel.add(Box.createHorizontalGlue());
        toolbarPanel.add(this.classloaderComboBox);
        toolbarPanel.add(Box.createHorizontalStrut(5));

        // 滚动条策略设置方式：JScrollPane、ScrollPaneConstants
        JBScrollPane runResultPanel = new JBScrollPane(runStatusArea);
        runResultPanel.setBorder(JBUI.Borders.empty());
        runResultPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        runResultPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        OnePixelSplitter resultSplitter = new OnePixelSplitter(true, "JZ.ConsoleRun.Result", 0.8f);
        resultSplitter.setBorder(JBUI.Borders.empty());
        resultSplitter.setFirstComponent(jvmResultPanel);
        resultSplitter.setSecondComponent(runResultPanel);

        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBorder(JBUI.Borders.empty());
        rootPanel.add(toolbarPanel, BorderLayout.NORTH);
        rootPanel.add(resultSplitter, BorderLayout.CENTER);
        return rootPanel;
    }

    private void autoAdjustComboBox(ComboBox<String> comboBox, List<String> items) {
        comboBox.removeAllItems();
        items.forEach(comboBox::addItem);
        // 获取 ComboBox 的 FontMetrics
        FontMetrics fontMetrics = comboBox.getFontMetrics(comboBox.getFont());
        // 计算最长文本的宽度
        int maxWidth = 0;
        for (String item : items) {
            int width = fontMetrics.stringWidth(item);
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        // 设置 ComboBox 的首选大小
        Dimension preferredSize = comboBox.getPreferredSize();
        preferredSize.width = maxWidth + comboBox.getInsets().left + comboBox.getInsets().right + 30;
        // 添加一些额外的空间
        comboBox.setPreferredSize(preferredSize);
        comboBox.setMaximumSize(preferredSize);
        comboBox.setMinimumSize(preferredSize);
    }

    public void refreshGroovyData(@Nullable String pid, @Nullable List<ClassloaderInfo> classloaderInfos) {
        if (pid != null) {
            this.pidLabel.setIcon(IconKit.SUCCESS);
            this.pidLabel.setText(pid);
        } else {
            this.pidLabel.setIcon(IconKit.FAIL);
            this.pidLabel.setText("未连接");
        }

        // 初始化选项
        classloaderInfos = Optional.ofNullable(classloaderInfos).orElse(new ArrayList<>());
        List<String> classLoaders = classloaderInfos.stream().map(ClassloaderInfo::getType).collect(Collectors.toList());
        this.autoAdjustComboBox(classloaderComboBox, classLoaders);
        // 设置默认选项
        classloaderInfos.stream().filter(ClassloaderInfo::getDefaultFlag)
                        .findFirst()
                        .map(ClassloaderInfo::getType)
                        .ifPresent(classloaderComboBox::setSelectedItem);
    }

    public void resetGroovyCode() {
        this.groovyEditor.setText(null);
        this.runStatusArea.setText(null);
        this.jvmResultPanel.refreshJvmResultTree(null);
    }

    public String getSelectClassloader() {
        return (String) classloaderComboBox.getSelectedItem();
    }

    public String getGroovyCode() {
        return groovyEditor.getText();
    }

    public void refreshJvmResult(@NotNull ObjectItem objectItem) {
        if (objectItem.getErrorStack() != null) {
            this.runStatusArea.setForeground(JBColor.RED);
            this.runStatusArea.setText(objectItem.getErrorStack());
            this.jvmResultPanel.refreshJvmResultTree(null);
        } else {
            this.runStatusArea.setForeground(JBColor.GREEN);
            this.runStatusArea.setText("Success");
            this.jvmResultPanel.refreshJvmResultTree(objectItem);
        }
    }

    public void refreshGroovyCode(String groovyCode) {
        this.groovyEditor.setText(groovyCode);
    }
}
