package com.finalshell.config;

/**
 * Proxy Configuration - HTTP/SOCKS/Jump Host proxy settings
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - SSH Proxy Configuration
 */
public class ProxyConfig {
    
    /**
     * Proxy type enumeration
     */
    public static final int TYPE_NONE = 0;
    public static final int TYPE_HTTP = 1;
    public static final int TYPE_SOCKS4 = 2;
    public static final int TYPE_SOCKS5 = 3;
    public static final int TYPE_JUMP_HOST = 4;
    
    public enum ProxyType {
        NONE("无代理"),
        HTTP("HTTP代理"),
        SOCKS4("SOCKS4代理"),
        SOCKS5("SOCKS5代理"),
        JUMP_HOST("跳板机");
        
        private final String displayName;
        
        ProxyType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private String id;
    private String name;
    private ProxyType type = ProxyType.NONE;
    
    // Proxy server settings (HTTP/SOCKS)
    private String proxyHost;
    private int proxyPort = 1080;
    private String proxyUsername;
    private String proxyPassword;
    
    // Jump host settings
    private String jumpHost;
    private int jumpPort = 22;
    private String jumpUsername;
    private String jumpPassword;
    private String jumpPrivateKey;
    
    public ProxyConfig() {
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public ProxyConfig(String name, ProxyType type) {
        this();
        this.name = name;
        this.type = type;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public ProxyType getType() { return type; }
    public void setType(ProxyType type) { this.type = type; }
    
    public void setType(String typeStr) {
        if (typeStr == null) { this.type = ProxyType.NONE; return; }
        switch (typeStr.toUpperCase()) {
            case "HTTP": this.type = ProxyType.HTTP; break;
            case "SOCKS4": this.type = ProxyType.SOCKS4; break;
            case "SOCKS5": this.type = ProxyType.SOCKS5; break;
            case "JUMP_HOST": case "跳板机": this.type = ProxyType.JUMP_HOST; break;
            default: this.type = ProxyType.NONE;
        }
    }
    
    public String getProxyHost() { return proxyHost; }
    public void setProxyHost(String proxyHost) { this.proxyHost = proxyHost; }
    
    public String getHost() { return proxyHost; }
    
    public int getPort() { return proxyPort; }
    
    public int getProxyPort() { return proxyPort; }
    public void setProxyPort(int proxyPort) { this.proxyPort = proxyPort; }
    
    public String getProxyUsername() { return proxyUsername; }
    public void setProxyUsername(String proxyUsername) { this.proxyUsername = proxyUsername; }
    
    public String getProxyPassword() { return proxyPassword; }
    public void setProxyPassword(String proxyPassword) { this.proxyPassword = proxyPassword; }
    
    public void setPassword(String password) { this.proxyPassword = password; }
    public String getPassword() { return proxyPassword; }
    public void setPort(int port) { this.proxyPort = port; }
    public void setUsername(String username) { this.proxyUsername = username; }
    public String getUsername() { return proxyUsername; }
    public void setHost(String host) { this.proxyHost = host; }
    
    public String getJumpHost() { return jumpHost; }
    public void setJumpHost(String jumpHost) { this.jumpHost = jumpHost; }
    
    public int getJumpPort() { return jumpPort; }
    public void setJumpPort(int jumpPort) { this.jumpPort = jumpPort; }
    
    public String getJumpUsername() { return jumpUsername; }
    public void setJumpUsername(String jumpUsername) { this.jumpUsername = jumpUsername; }
    
    public String getJumpPassword() { return jumpPassword; }
    public void setJumpPassword(String jumpPassword) { this.jumpPassword = jumpPassword; }
    
    public String getJumpPrivateKey() { return jumpPrivateKey; }
    public void setJumpPrivateKey(String jumpPrivateKey) { this.jumpPrivateKey = jumpPrivateKey; }
    
    /**
     * Check if proxy is enabled
     */
    public boolean isEnabled() {
        return type != ProxyType.NONE;
    }
    
    /**
     * Check if this is a jump host proxy
     */
    public boolean isJumpHost() {
        return type == ProxyType.JUMP_HOST;
    }
    
    /**
     * Get display string
     */
    public String getDisplayString() {
        if (type == ProxyType.NONE) {
            return "无";
        } else if (type == ProxyType.JUMP_HOST) {
            return String.format("%s@%s:%d", jumpUsername, jumpHost, jumpPort);
        } else {
            return String.format("%s:%d (%s)", proxyHost, proxyPort, type.getDisplayName());
        }
    }
    
    @Override
    public String toString() {
        return name != null ? name : getDisplayString();
    }
}
