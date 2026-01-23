package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 表格内嵌面板基类
 */
public class PanelInTable extends JPanel {
    
    protected boolean selected = false;
    protected boolean focused = false;
    
    public PanelInTable() {
        setOpaque(true);
        setLayout(new BorderLayout());
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        updateBackground();
    }
    
    public void setFocused(boolean focused) {
        this.focused = focused;
        updateBackground();
    }
    
    protected void updateBackground() {
        if (selected) {
            setBackground(UIManager.getColor("Table.selectionBackground"));
        } else {
            setBackground(UIManager.getColor("Table.background"));
        }
    }
}
