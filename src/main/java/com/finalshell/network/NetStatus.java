package com.finalshell.network;

/**
 * 网络状态
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetStatus {
    
    public static final int STATUS_UNKNOWN = 0;
    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_DISCONNECTED = 2;
    public static final int STATUS_CONNECTING = 3;
    public static final int STATUS_ERROR = 4;
    
    private int status;
    private String message;
    private long lastUpdateTime;
    private long bytesReceived;
    private long bytesSent;
    private int latency;
    
    public NetStatus() {
        this.status = STATUS_UNKNOWN;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public long getBytesReceived() {
        return bytesReceived;
    }
    
    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }
    
    public long getBytesSent() {
        return bytesSent;
    }
    
    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }
    
    public int getLatency() {
        return latency;
    }
    
    public void setLatency(int latency) {
        this.latency = latency;
    }
    
    public String getStatusString() {
        switch (status) {
            case STATUS_CONNECTED: return "已连接";
            case STATUS_DISCONNECTED: return "已断开";
            case STATUS_CONNECTING: return "连接中";
            case STATUS_ERROR: return "错误";
            default: return "未知";
        }
    }
    
    public boolean isConnected() {
        return status == STATUS_CONNECTED;
    }
}
