package com.finalshell.config;

/**
 * Port Forward Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DataModel_ConfigFormat.md
 */
public class PortForwardConfig {
    
    public enum Type {
        LOCAL,      // Local port forward: -L
        REMOTE,     // Remote port forward: -R
        DYNAMIC     // Dynamic port forward (SOCKS): -D
    }
    
    private String id;
    private Type type = Type.LOCAL;
    private String name;
    private boolean enabled = true;
    
    // Local settings
    private String localHost = "127.0.0.1";
    private int localPort;
    
    // Remote settings
    private String remoteHost;
    private int remotePort;
    
    public PortForwardConfig() {
    }
    
    public PortForwardConfig(Type type, int localPort, String remoteHost, int remotePort) {
        this.type = type;
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }
    
    // Getters and Setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
    
    @Override
    public String toString() {
        if (name != null && !name.isEmpty()) {
            return name;
        }
        switch (type) {
            case LOCAL:
                return String.format("L:%d -> %s:%d", localPort, remoteHost, remotePort);
            case REMOTE:
                return String.format("R:%d <- %s:%d", remotePort, localHost, localPort);
            case DYNAMIC:
                return String.format("D:%d (SOCKS)", localPort);
            default:
                return "Unknown";
        }
    }
}
