package com.finalshell.proxy;

/**
 * 代理信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyInfo {
    
    public static final int TYPE_NONE = 0;
    public static final int TYPE_HTTP = 1;
    public static final int TYPE_SOCKS4 = 2;
    public static final int TYPE_SOCKS5 = 3;
    
    private String id;
    private String name;
    private int type;
    private String host;
    private int port;
    private String username;
    private String password;
    private boolean defaultProxy;
    
    public ProxyInfo() {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = TYPE_SOCKS5;
        this.port = 1080;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isDefaultProxy() { return defaultProxy; }
    public void setDefaultProxy(boolean defaultProxy) { this.defaultProxy = defaultProxy; }
    
    public String getTypeString() {
        switch (type) {
            case TYPE_NONE: return "无";
            case TYPE_HTTP: return "HTTP";
            case TYPE_SOCKS4: return "SOCKS4";
            case TYPE_SOCKS5: return "SOCKS5";
            default: return "未知";
        }
    }
    
    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
