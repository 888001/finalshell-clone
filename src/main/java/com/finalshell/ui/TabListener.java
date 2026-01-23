package com.finalshell.ui;

/**
 * 标签事件监听器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - TabListener
 */
public interface TabListener {
    
    void onTabSelected(TabEvent event);
    
    void onTabClose(TabEvent event);
    
    default void onTabAdded(TabEvent event) {}
    
    default void onTabMoved(TabEvent event) {}
}
