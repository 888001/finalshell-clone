package com.finalshell.portmap;

/**
 * 端口映射规则
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MapRule {
    
    private String id;
    private String name;
    private String type;
    private String localHost;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private boolean running;
    private String description;
    
    public MapRule() {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = "LOCAL";
        this.localHost = "127.0.0.1";
        this.remoteHost = "127.0.0.1";
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getLocalHost() {
        return localHost;
    }
    
    public void setLocalHost(String localHost) {
        this.localHost = localHost;
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
    
    public boolean isRunning() {
        return running;
    }
    
    public void setRunning(boolean running) {
        this.running = running;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return String.format("%s:%d -> %s:%d", localHost, localPort, remoteHost, remotePort);
    }
}
