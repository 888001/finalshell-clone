package com.finalshell.network;

/**
 * 网络套接字信息
 */
public class NetSocket {
    
    private String protocol;
    private String localAddress;
    private int localPort;
    private String remoteAddress;
    private int remotePort;
    private String state;
    
    public NetSocket() {}
    
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    
    public String getLocalAddress() { return localAddress; }
    public void setLocalAddress(String localAddress) { this.localAddress = localAddress; }
    
    public int getLocalPort() { return localPort; }
    public void setLocalPort(int localPort) { this.localPort = localPort; }
    
    public String getRemoteAddress() { return remoteAddress; }
    public void setRemoteAddress(String remoteAddress) { this.remoteAddress = remoteAddress; }
    
    public int getRemotePort() { return remotePort; }
    public void setRemotePort(int remotePort) { this.remotePort = remotePort; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
