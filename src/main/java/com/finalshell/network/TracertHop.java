package com.finalshell.network;

/**
 * 路由跟踪跳点信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertHop {
    
    private int hopNumber;
    private String ipAddress;
    private String hostname;
    private double latency;
    private String location;
    private boolean timeout;
    
    public TracertHop() {
    }
    
    public TracertHop(int hopNumber, String ipAddress, double latency) {
        this.hopNumber = hopNumber;
        this.ipAddress = ipAddress;
        this.latency = latency;
        this.timeout = false;
    }
    
    public static TracertHop timeout(int hopNumber) {
        TracertHop hop = new TracertHop();
        hop.hopNumber = hopNumber;
        hop.timeout = true;
        return hop;
    }
    
    public int getHopNumber() {
        return hopNumber;
    }
    
    public void setHopNumber(int hopNumber) {
        this.hopNumber = hopNumber;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    
    public double getLatency() {
        return latency;
    }
    
    public void setLatency(double latency) {
        this.latency = latency;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public boolean isTimeout() {
        return timeout;
    }
    
    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }
}
