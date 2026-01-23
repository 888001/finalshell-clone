package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 表格内嵌面板变体3
 * 带按钮的面板
 */
public class PanelInTable3 extends PanelInTable {
    
    private JLabel textLabel;
    private JButton actionButton;
    
    public PanelInTable3() {
        super();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        textLabel = new JLabel();
        actionButton = new JButton("操作");
        actionButton.setMargin(new Insets(0, 5, 0, 5));
        add(textLabel, BorderLayout.CENTER);
        add(actionButton, BorderLayout.EAST);
    }
    
    public void setText(String text) {
        textLabel.setText(text);
    }
    
    public void setButtonText(String text) {
        actionButton.setText(text);
    }
    
    public JButton getActionButton() {
        return actionButton;
    }
}
