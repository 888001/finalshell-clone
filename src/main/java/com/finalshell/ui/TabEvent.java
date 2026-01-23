package com.finalshell.ui;

/**
 * 标签事件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - TabEvent
 */
public class TabEvent {
    
    public static final int TYPE_SELECT = 1;
    public static final int TYPE_CLOSE = 2;
    public static final int TYPE_ADD = 3;
    public static final int TYPE_MOVE = 4;
    
    private int type;
    private TabWrap tabWrap;
    private int index;
    
    public TabEvent(int type, TabWrap tabWrap) {
        this.type = type;
        this.tabWrap = tabWrap;
    }
    
    public TabEvent(int type, TabWrap tabWrap, int index) {
        this.type = type;
        this.tabWrap = tabWrap;
        this.index = index;
    }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public TabWrap getTabWrap() { return tabWrap; }
    public void setTabWrap(TabWrap tabWrap) { this.tabWrap = tabWrap; }
    
    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
}
