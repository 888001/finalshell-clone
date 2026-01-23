package com.finalshell.network;

/**
 * Traceroute节点数据
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertNode {
    
    private int hop;
    private String ipAddress;
    private String hostname;
    private long rtt1;
    private long rtt2;
    private long rtt3;
    private String rawLine;
    private String location;
    
    public TracertNode() {
    }
    
    public TracertNode(int hop, String ipAddress, long rtt1) {
        this.hop = hop;
        this.ipAddress = ipAddress;
        this.rtt1 = rtt1;
    }
    
    public int getHop() {
        return hop;
    }
    
    public void setHop(int hop) {
        this.hop = hop;
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
    
    public long getRtt1() {
        return rtt1;
    }
    
    public void setRtt1(long rtt1) {
        this.rtt1 = rtt1;
    }
    
    public long getRtt2() {
        return rtt2;
    }
    
    public void setRtt2(long rtt2) {
        this.rtt2 = rtt2;
    }
    
    public long getRtt3() {
        return rtt3;
    }
    
    public void setRtt3(long rtt3) {
        this.rtt3 = rtt3;
    }
    
    public String getRawLine() {
        return rawLine;
    }
    
    public void setRawLine(String rawLine) {
        this.rawLine = rawLine;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public long getAverageRtt() {
        int count = 0;
        long total = 0;
        if (rtt1 > 0) { total += rtt1; count++; }
        if (rtt2 > 0) { total += rtt2; count++; }
        if (rtt3 > 0) { total += rtt3; count++; }
        return count > 0 ? total / count : 0;
    }
    
    @Override
    public String toString() {
        return String.format("%d: %s (%d ms)", hop, ipAddress, getAverageRtt());
    }
}
