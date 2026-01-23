package com.finalshell.monitor;

/**
 * 速度数据包装类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SpeedWrap {
    
    private long rxBytes;
    private long txBytes;
    private double rxSpeed;
    private double txSpeed;
    private long timestamp;
    
    public SpeedWrap() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public SpeedWrap(long rxBytes, long txBytes) {
        this.rxBytes = rxBytes;
        this.txBytes = txBytes;
        this.timestamp = System.currentTimeMillis();
    }
    
    public SpeedWrap(double rxSpeed, double txSpeed) {
        this.rxSpeed = rxSpeed;
        this.txSpeed = txSpeed;
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getRxBytes() {
        return rxBytes;
    }
    
    public void setRxBytes(long rxBytes) {
        this.rxBytes = rxBytes;
    }
    
    public long getTxBytes() {
        return txBytes;
    }
    
    public void setTxBytes(long txBytes) {
        this.txBytes = txBytes;
    }
    
    public double getRxSpeed() {
        return rxSpeed;
    }
    
    public void setRxSpeed(double rxSpeed) {
        this.rxSpeed = rxSpeed;
    }
    
    public double getTxSpeed() {
        return txSpeed;
    }
    
    public void setTxSpeed(double txSpeed) {
        this.txSpeed = txSpeed;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String formatRxSpeed() {
        return formatSpeed(rxSpeed);
    }
    
    public String formatTxSpeed() {
        return formatSpeed(txSpeed);
    }
    
    private String formatSpeed(double bytesPerSec) {
        if (bytesPerSec < 1024) {
            return String.format("%.0f B/s", bytesPerSec);
        } else if (bytesPerSec < 1024 * 1024) {
            return String.format("%.1f KB/s", bytesPerSec / 1024);
        } else {
            return String.format("%.2f MB/s", bytesPerSec / (1024 * 1024));
        }
    }
}
