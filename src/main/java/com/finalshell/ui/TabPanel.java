package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 标签页面板
 * 标签页内容容器
 */
public class TabPanel extends JPanel {
    
    private String tabId;
    private String tabTitle;
    private Icon tabIcon;
    private boolean closeable = true;
    private boolean modified = false;
    
    public TabPanel() {
        this(null, null);
    }
    
    public TabPanel(String title) {
        this(title, null);
    }
    
    public TabPanel(String title, Icon icon) {
        this.tabTitle = title;
        this.tabIcon = icon;
        this.tabId = java.util.UUID.randomUUID().toString();
        setLayout(new BorderLayout());
    }
    
    public String getTabId() { return tabId; }
    public void setTabId(String tabId) { this.tabId = tabId; }
    
    public String getTabTitle() { return tabTitle; }
    public void setTabTitle(String tabTitle) { this.tabTitle = tabTitle; }
    
    public Icon getTabIcon() { return tabIcon; }
    public void setTabIcon(Icon tabIcon) { this.tabIcon = tabIcon; }
    
    public boolean isCloseable() { return closeable; }
    public void setCloseable(boolean closeable) { this.closeable = closeable; }
    
    public boolean isModified() { return modified; }
    public void setModified(boolean modified) { this.modified = modified; }
    
    public void onTabSelected() {}
    public void onTabDeselected() {}
    public void onTabClosing() {}
    public void onTabClosed() {}
}
