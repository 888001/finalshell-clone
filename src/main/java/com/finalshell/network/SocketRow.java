package com.finalshell.network;

/**
 * Socket连接行数据
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SocketRow {
    
    private String type;
    private String protocol;
    private String localAddress;
    private int localPort;
    private String remoteAddress;
    private int remotePort;
    private String state;
    private int inode;
    private String path;
    private int pid;
    private String processName;
    
    public SocketRow() {
    }
    
    public SocketRow(String type, String localAddress, int localPort, String state) {
        this.type = type;
        this.localAddress = localAddress;
        this.localPort = localPort;
        this.state = state;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
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
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public int getInode() {
        return inode;
    }
    
    public void setInode(int inode) {
        this.inode = inode;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
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
    
    public String getProtocol() { return protocol != null ? protocol : type; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    
    public String getRemoteAddress() { return remoteAddress; }
    public void setRemoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; }
    
    public int getRemotePort() { return remotePort; }
    public void setRemotePort(int remotePort) { this.remotePort = remotePort; }
    
    @Override
    public String toString() {
        return String.format("%s %s:%d [%s]", type, localAddress, localPort, state);
    }
}
