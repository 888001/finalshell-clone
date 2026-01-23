package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 标签页面板基类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - BaseTabPanel
 */
public class BaseTabPanel extends JPanel {
    
    protected TabWrap tabWrap;
    protected String title;
    protected Icon icon;
    protected boolean closeable = true;
    
    public BaseTabPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
    }
    
    public BaseTabPanel(String title) {
        this();
        this.title = title;
    }
    
    public void setTabWrap(TabWrap tabWrap) {
        this.tabWrap = tabWrap;
    }
    
    public TabWrap getTabWrap() {
        return tabWrap;
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public Icon getIcon() { return icon; }
    public void setIcon(Icon icon) { this.icon = icon; }
    
    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }
    
    public void onActivated() {
        // 子类重写
    }
    
    public void onDeactivated() {
        // 子类重写
    }
    
    public void onClose() {
        // 子类重写
    }
}
