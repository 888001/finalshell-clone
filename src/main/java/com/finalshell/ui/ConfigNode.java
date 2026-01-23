package com.finalshell.ui;

import javax.swing.*;

/**
 * 配置树节点
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - ConfigNode
 */
public class ConfigNode {
    
    private String name;
    private String key;
    private Object value;
    private Icon icon;
    private JComponent panel;
    
    public ConfigNode(String name) {
        this.name = name;
    }
    
    public ConfigNode(String name, String key, Object value) {
        this.name = name;
        this.key = key;
        this.value = value;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    
    public Icon getIcon() { return icon; }
    public void setIcon(Icon icon) { this.icon = icon; }
    
    public JComponent getPanel() { return panel; }
    public void setPanel(JComponent panel) { this.panel = panel; }
    
    @Override
    public String toString() {
        return name;
    }
}
