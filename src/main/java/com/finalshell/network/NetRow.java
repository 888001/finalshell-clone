package com.finalshell.network;

/**
 * 网络连接行数据
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetRow {
    
    private String protocol;
    private String localAddress;
    private int localPort;
    private String remoteAddress;
    private int remotePort;
    private String state;
    private int pid;
    private String processName;
    private long rxBytes;
    private long txBytes;
    
    public NetRow() {
    }
    
    public NetRow(String protocol, String localAddress, int localPort, 
                  String remoteAddress, int remotePort, String state) {
        this.protocol = protocol;
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.state = state;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public String getLocalAddress() {
        return localAddress;
    }
    
    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }
    
    public int getLocalPort() {
        return localPort;
    }
    
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    
    public String getRemoteAddress() {
        return remoteAddress;
    }
    
    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
    
    public int getRemotePort() {
        return remotePort;
    }
    
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid) {
        this.pid = pid;
    }
    
    public String getProcessName() {
        return processName;
    }
    
    public void setProcessName(String processName) {
        this.processName = processName;
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
    
    public String getLocalEndpoint() {
        return localAddress + ":" + localPort;
    }
    
    public String getRemoteEndpoint() {
        return remoteAddress + ":" + remotePort;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s -> %s [%s]", 
            protocol, getLocalEndpoint(), getRemoteEndpoint(), state);
    }
}
