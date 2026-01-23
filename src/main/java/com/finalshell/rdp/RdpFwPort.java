package com.finalshell.rdp;

/**
 * RDP端口转发配置
 * 存储RDP端口转发的配置信息
 */
public class RdpFwPort {
    
    private String id;
    private String name;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private boolean enabled;
    private boolean autoStart;
    
    public RdpFwPort() {
        this.remotePort = 3389;
        this.enabled = true;
    }
    
    public RdpFwPort(int localPort, String remoteHost, int remotePort) {
        this();
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getLocalPort() {
        return localPort;
    }
    
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    
    public String getRemoteHost() {
        return remoteHost;
    }
    
    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
    
    public int getRemotePort() {
        return remotePort;
    }
    
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }
    
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
    
    public String getForwardRule() {
        return String.format("localhost:%d -> %s:%d", localPort, remoteHost, remotePort);
    }
    
    @Override
    public String toString() {
        return name != null ? name : getForwardRule();
    }
}
