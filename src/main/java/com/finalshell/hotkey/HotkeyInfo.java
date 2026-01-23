package com.finalshell.hotkey;

import javax.swing.*;

/**
 * 快捷键信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class HotkeyInfo {
    
    private String id;
    private String name;
    private String action;
    private KeyStroke keyStroke;
    private boolean enabled;
    private String category;
    
    public HotkeyInfo() {
        this.id = java.util.UUID.randomUUID().toString();
        this.enabled = true;
    }
    
    public HotkeyInfo(String name, String action, KeyStroke keyStroke) {
        this();
        this.name = name;
        this.action = action;
        this.keyStroke = keyStroke;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public KeyStroke getKeyStroke() {
        return keyStroke;
    }
    
    public void setKeyStroke(KeyStroke keyStroke) {
        this.keyStroke = keyStroke;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getKeyStrokeText() {
        if (keyStroke == null) return "";
        return keyStroke.toString().replace("pressed ", "");
    }
    
    @Override
    public String toString() {
        return name + " (" + getKeyStrokeText() + ")";
    }
}
