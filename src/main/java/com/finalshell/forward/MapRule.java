package com.finalshell.forward;

/**
 * SSH加速映射规则
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSHTunnel_UI_DeepAnalysis.md
 */
public class MapRule {
    
    private String id;
    private String name;
    private String targetHost;
    private int targetPort;
    private int localPort;
    private boolean enabled;
    private String connectConfigId;
    private long createTime;
    private long modifyTime;
    
    public MapRule() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createTime = System.currentTimeMillis();
        this.modifyTime = this.createTime;
        this.enabled = true;
    }
    
    public MapRule(String name, String targetHost, int targetPort, int localPort) {
        this();
        this.name = name;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.localPort = localPort;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public String getTargetHost() { return targetHost; }
    public void setTargetHost(String targetHost) { 
        this.targetHost = targetHost;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public int getTargetPort() { return targetPort; }
    public void setTargetPort(int targetPort) { 
        this.targetPort = targetPort;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public int getLocalPort() { return localPort; }
    public void setLocalPort(int localPort) { 
        this.localPort = localPort;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { 
        this.enabled = enabled;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public String getConnectConfigId() { return connectConfigId; }
    public void setConnectConfigId(String connectConfigId) { 
        this.connectConfigId = connectConfigId; 
    }
    
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    
    public long getModifyTime() { return modifyTime; }
    public void setModifyTime(long modifyTime) { this.modifyTime = modifyTime; }
    
    @Override
    public String toString() {
        return name + " (localhost:" + localPort + " -> " + targetHost + ":" + targetPort + ")";
    }
}
