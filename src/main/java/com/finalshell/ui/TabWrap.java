package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 标签包装器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - TabWrap
 */
public class TabWrap {
    
    private BaseTabPanel panel;
    private TabButton tabButton;
    private String id;
    private String title;
    private Icon icon;
    private boolean active;
    private boolean closeable = true;
    private Object userData;
    
    public TabWrap(BaseTabPanel panel) {
        this.panel = panel;
        this.title = panel.getTitle();
        this.icon = panel.getIcon();
        panel.setTabWrap(this);
    }
    
    public TabWrap(String title, BaseTabPanel panel) {
        this.panel = panel;
        this.title = title;
        panel.setTabWrap(this);
    }
    
    public BaseTabPanel getPanel() { return panel; }
    public void setPanel(BaseTabPanel panel) { this.panel = panel; }
    
    public TabButton getTabButton() { return tabButton; }
    public void setTabButton(TabButton tabButton) { this.tabButton = tabButton; }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title;
        if (tabButton != null) {
            tabButton.setText(title);
        }
    }
    
    public Icon getIcon() { return icon; }
    public void setIcon(Icon icon) { 
        this.icon = icon;
        if (tabButton != null) {
            tabButton.setIcon(icon);
        }
    }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }
    
    public Object getUserData() { return userData; }
    public void setUserData(Object userData) { this.userData = userData; }
}
