package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 表格内嵌面板变体4
 * 带复选框的面板
 */
public class PanelInTable4 extends PanelInTable {
    
    private JCheckBox checkBox;
    private JLabel textLabel;
    
    public PanelInTable4() {
        super();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        textLabel = new JLabel();
        add(checkBox, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }
    
    public void setChecked(boolean checked) {
        checkBox.setSelected(checked);
    }
    
    public boolean isChecked() {
        return checkBox.isSelected();
    }
    
    public void setText(String text) {
        textLabel.setText(text);
    }
    
    public JCheckBox getCheckBox() {
        return checkBox;
    }
}
