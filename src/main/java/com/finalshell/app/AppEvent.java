package com.finalshell.app;

/**
 * 应用程序事件
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AppEvent {
    
    public static final int TYPE_APP_STARTED = 1;
    public static final int TYPE_APP_CLOSING = 2;
    public static final int TYPE_CONFIG_CHANGED = 3;
    public static final int TYPE_THEME_CHANGED = 4;
    public static final int TYPE_FONT_CHANGED = 5;
    public static final int TYPE_LICENSE_CHANGED = 6;
    public static final int TYPE_SESSION_OPENED = 7;
    public static final int TYPE_SESSION_CLOSED = 8;
    
    private int type;
    private Object data;
    private long timestamp;
    
    public AppEvent(int type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    public AppEvent(int type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getTypeName() {
        switch (type) {
            case TYPE_APP_STARTED: return "APP_STARTED";
            case TYPE_APP_CLOSING: return "APP_CLOSING";
            case TYPE_CONFIG_CHANGED: return "CONFIG_CHANGED";
            case TYPE_THEME_CHANGED: return "THEME_CHANGED";
            case TYPE_FONT_CHANGED: return "FONT_CHANGED";
            case TYPE_LICENSE_CHANGED: return "LICENSE_CHANGED";
            case TYPE_SESSION_OPENED: return "SESSION_OPENED";
            case TYPE_SESSION_CLOSED: return "SESSION_CLOSED";
            default: return "UNKNOWN";
        }
    }
    
    @Override
    public String toString() {
        return "AppEvent{type=" + getTypeName() + ", data=" + data + "}";
    }
}
