package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 表格内嵌面板变体1
 * 带图标和文本的面板
 */
public class PanelInTable1 extends PanelInTable {
    
    private JLabel iconLabel;
    private JLabel textLabel;
    
    public PanelInTable1() {
        super();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 0));
        iconLabel = new JLabel();
        textLabel = new JLabel();
        add(iconLabel, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
    }
    
    public void setIcon(Icon icon) {
        iconLabel.setIcon(icon);
    }
    
    public void setText(String text) {
        textLabel.setText(text);
    }
    
    @Override
    protected void updateBackground() {
        super.updateBackground();
        Color fg = selected ? UIManager.getColor("Table.selectionForeground") 
                           : UIManager.getColor("Table.foreground");
        textLabel.setForeground(fg);
    }
}
