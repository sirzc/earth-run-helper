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

package com.myth.earth.run.plugin.dialog;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.ui.border.CustomLineBorder;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.myth.earth.run.kit.ProjectRunKit;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.VerticalLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class VirtualSelectDialog extends JDialog {
    private final Project      project;
    private       JPanel       contentPane;
    private       JButton      buttonCancel;
    private       JPanel       showPanel;
    private       JBScrollPane jbScrollPane;
    @Getter
    private       String       pid;

    public VirtualSelectDialog(@NotNull Project project) {
        this.project = project;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        setSize(450, 200);
        setTitle("Virtual Select");
        setLocationRelativeTo(null);

        // buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // 点击 X 时调用 onCancel()
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // 遇到 ESCAPE 时调用 onCancel()
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        EmptyBorder emptyBorder = new EmptyBorder(JBUI.emptyInsets());
        jbScrollPane.setBorder(emptyBorder);
        showPanel.setBorder(emptyBorder);
        showPanel.add(getVirtualComponent(), BorderLayout.CENTER);
    }

    private void onCancel() {
        // 必要时在此处添加您的代码
        dispose();
    }

    public JComponent getVirtualComponent() {
        Border topEmptyBorder = BorderFactory.createEmptyBorder(5, 0, 0, 0);
        Border leftEmptyBorder = BorderFactory.createEmptyBorder(0, 5, 0, 0);
        JPanel verticalBox = new JPanel(new VerticalLayout());
        // Collection<AgentAttachService.JvmItem> jvmItems = AgentAttachService.getInstance().jvmList();
        Map<Long, String> running = ProjectRunKit.getMapRunning(project);
        for (Map.Entry<Long, String> entry : running.entrySet()) {
            Long pid = entry.getKey();
            String displayName = entry.getValue() + "(" + pid + ")";
            if (StringUtils.isBlank(displayName)) {
                continue;
            }

            if (displayName.startsWith("org.jetbrains") || displayName.startsWith("com.intellij")) {
                continue;
            }

            JBLabel displayNameLabel = new JBLabel(displayName);
            displayNameLabel.setBorder(leftEmptyBorder);

            JPanel jPanel = new JPanel(new BorderLayout());
            jPanel.setBorder(new CustomLineBorder(JBUI.insets(1)));
            jPanel.add(displayNameLabel, BorderLayout.WEST);
            jPanel.add(createButton(AllIcons.Actions.Install, String.valueOf(pid)), BorderLayout.EAST);

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBorder(topEmptyBorder);
            topPanel.add(jPanel, BorderLayout.CENTER);
            verticalBox.add(topPanel);
        }
        return verticalBox;
    }

    /**
     * 创建一个图标按钮
     *
     * @param icon                     图标
     * @param virtualMachineDescriptor 虚拟机信息
     * @return 图标按钮
     */
    public JButton createButton(@NotNull Icon icon, @NotNull String pid) {
        JButton jButton = new JButton();
        jButton.setIcon(icon);
        // 去掉默认背景填充
        jButton.setContentAreaFilled(false);
        jButton.setBorder(BorderFactory.createEmptyBorder());
        jButton.setPreferredSize(new Dimension(30, 30));
        jButton.addActionListener(actionEvent -> {
            this.pid = pid;
            onCancel();
        });
        return jButton;
    }

}
