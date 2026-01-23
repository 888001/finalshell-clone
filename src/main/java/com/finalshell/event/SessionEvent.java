package com.finalshell.event;

/**
 * SSH会话事件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Tab_Log_Misc_Analysis.md
 */
public class SessionEvent {
    
    public static final int CONNECTED = 100;
    public static final int DISCONNECTED = 101;
    public static final int AUTH_FAILED = 102;
    public static final int TIMEOUT = 103;
    public static final int ERROR = 104;
    public static final int RECONNECTING = 105;
    public static final int SHELL_READY = 106;
    
    private final int type;
    private final String sessionId;
    private final String message;
    private final Throwable exception;
    private final long timestamp;
    
    public SessionEvent(int type, String sessionId) {
        this(type, sessionId, null, null);
    }
    
    public SessionEvent(int type, String sessionId, String message) {
        this(type, sessionId, message, null);
    }
    
    public SessionEvent(int type, String sessionId, String message, Throwable exception) {
        this.type = type;
        this.sessionId = sessionId;
        this.message = message;
        this.exception = exception;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getType() { return type; }
    public String getSessionId() { return sessionId; }
    public String getMessage() { return message; }
    public Throwable getException() { return exception; }
    public long getTimestamp() { return timestamp; }
    
    public boolean isConnected() { return type == CONNECTED; }
    public boolean isDisconnected() { return type == DISCONNECTED; }
    public boolean isError() { return type == ERROR || type == AUTH_FAILED || type == TIMEOUT; }
    
    public String getTypeString() {
        switch (type) {
            case CONNECTED: return "已连接";
            case DISCONNECTED: return "已断开";
            case AUTH_FAILED: return "认证失败";
            case TIMEOUT: return "连接超时";
            case ERROR: return "错误";
            case RECONNECTING: return "重连中";
            case SHELL_READY: return "Shell就绪";
            default: return "未知";
        }
    }
    
    @Override
    public String toString() {
        return String.format("SessionEvent[%s, session=%s, message=%s]", 
            getTypeString(), sessionId, message);
    }
}
