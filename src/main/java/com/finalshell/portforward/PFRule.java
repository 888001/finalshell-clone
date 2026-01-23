package com.finalshell.portforward;

/**
 * 端口转发规则
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFRule {
    
    public static final int TYPE_LOCAL = 0;
    public static final int TYPE_REMOTE = 1;
    public static final int TYPE_DYNAMIC = 2;
    
    private String id;
    private String name;
    private int type;
    private String localHost;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private boolean running;
    private String description;
    
    public PFRule() {
        this.id = java.util.UUID.randomUUID().toString();
        this.localHost = "127.0.0.1";
        this.remoteHost = "127.0.0.1";
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public String getLocalHost() { return localHost; }
    public void setLocalHost(String localHost) { this.localHost = localHost; }
    
    public int getLocalPort() { return localPort; }
    public void setLocalPort(int localPort) { this.localPort = localPort; }
    
    public String getRemoteHost() { return remoteHost; }
    public void setRemoteHost(String remoteHost) { this.remoteHost = remoteHost; }
    
    public int getRemotePort() { return remotePort; }
    public void setRemotePort(int remotePort) { this.remotePort = remotePort; }
    
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getTypeString() {
        switch (type) {
            case TYPE_LOCAL: return "本地转发";
            case TYPE_REMOTE: return "远程转发";
            case TYPE_DYNAMIC: return "动态转发";
            default: return "未知";
        }
    }
}
