package com.finalshell.ui;

/**
 * 应用事件类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md - AppEvent
 */
public class AppEvent {
    
    public static final int TYPE_CONFIG_CHANGED = 1;
    public static final int TYPE_CONNECTION_CHANGED = 2;
    public static final int TYPE_COMMAND_CHANGED = 3;
    public static final int TYPE_THEME_CHANGED = 4;
    public static final int TYPE_LANGUAGE_CHANGED = 5;
    public static final int TYPE_FOLDER_CHANGED = 6;
    
    private int type;
    private Object source;
    private Object data;
    
    public AppEvent(int type) {
        this.type = type;
    }
    
    public AppEvent(int type, Object source) {
        this.type = type;
        this.source = source;
    }
    
    public AppEvent(int type, Object source, Object data) {
        this.type = type;
        this.source = source;
        this.data = data;
    }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public Object getSource() { return source; }
    public void setSource(Object source) { this.source = source; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
}
