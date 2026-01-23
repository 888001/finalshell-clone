package com.finalshell.ui;

import javax.swing.*;

/**
 * 弹出菜单项数据
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - PopupItem
 */
public class PopupItem {
    
    private String text;
    private Icon icon;
    private String command;
    private boolean enabled = true;
    private boolean separator = false;
    
    public PopupItem(String text) {
        this.text = text;
    }
    
    public PopupItem(String text, Icon icon) {
        this.text = text;
        this.icon = icon;
    }
    
    public PopupItem(String text, String command) {
        this.text = text;
        this.command = command;
    }
    
    public static PopupItem separator() {
        PopupItem item = new PopupItem(null);
        item.separator = true;
        return item;
    }
    
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    
    public Icon getIcon() { return icon; }
    public void setIcon(Icon icon) { this.icon = icon; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isSeparator() { return separator; }
}
