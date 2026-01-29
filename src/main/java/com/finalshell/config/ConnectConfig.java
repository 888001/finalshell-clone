package com.finalshell.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Connection Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DataModel_ConfigFormat.md - ConnectConfig
 */
public class ConnectConfig implements Cloneable {
    
    // Basic info
    private String id;
    private String name;
    private String parentId;
    private int type = 1; // 1=SSH, 2=RDP, 3=Port Forward
    private long createTime;
    private long updateTime;
    private long lastConnectTime;
    
    // Connection settings
    private String host;
    private int port = 22;
    private String userName;
    private String password;
    private String privateKey;
    private String passphrase;
    
    // Terminal settings
    private String charset = "UTF-8";
    private int timeout = 30000;
    private boolean compression = false;
    private int keepAliveInterval = 60;
    private String terminalType = "xterm-256color";
    private int terminalCols = 80;
    private int terminalRows = 24;
    private boolean enableCompression = false;
    
    // Proxy settings
    private String proxyId;
    private boolean useJumpServer = false;
    private String jumpServerId;
    private ProxyConfig proxyConfig;
    
    // Port forwards
    private List<PortForwardConfig> portForwards = new ArrayList<>();
    
    // Startup commands
    private List<String> startupCommands = new ArrayList<>();
    
    // Display settings
    private int color = 0;
    private String icon;
    private String memo;
    
    // RDP specific
    private int rdpWidth = 1920;
    private int rdpHeight = 1080;
    private boolean rdpFullscreen = false;
    
    public ConnectConfig() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    
    @Override
    public ConnectConfig clone() {
        try {
            ConnectConfig cloned = (ConnectConfig) super.clone();
            cloned.portForwards = new ArrayList<>(this.portForwards);
            cloned.startupCommands = new ArrayList<>(this.startupCommands);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    
    // Getters and Setters
    
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
        this.updateTime = System.currentTimeMillis();
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    
    public long getLastConnectTime() {
        return lastConnectTime;
    }
    
    public void setLastConnectTime(long lastConnectTime) {
        this.lastConnectTime = lastConnectTime;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
        this.updateTime = System.currentTimeMillis();
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getPassphrase() {
        return passphrase;
    }
    
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
    
    public String getCharset() {
        return charset;
    }
    
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    public int getTimeout() {
        return timeout;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public boolean isCompression() {
        return compression;
    }
    
    public void setCompression(boolean compression) {
        this.compression = compression;
    }
    
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }
    
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public String getProxyId() {
        return proxyId;
    }
    
    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }
    
    public boolean isUseJumpServer() {
        return useJumpServer;
    }
    
    public void setUseJumpServer(boolean useJumpServer) {
        this.useJumpServer = useJumpServer;
    }
    
    public String getJumpServerId() {
        return jumpServerId;
    }
    
    public void setJumpServerId(String jumpServerId) {
        this.jumpServerId = jumpServerId;
    }
    
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }
    
    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }
    
    public List<PortForwardConfig> getPortForwards() {
        return portForwards;
    }
    
    public void setPortForwards(List<PortForwardConfig> portForwards) {
        this.portForwards = portForwards;
    }
    
    public List<String> getStartupCommands() {
        return startupCommands;
    }
    
    public void setStartupCommands(List<String> startupCommands) {
        this.startupCommands = startupCommands;
    }
    
    public int getColor() {
        return color;
    }
    
    public void setColor(int color) {
        this.color = color;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getMemo() {
        return memo;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public int getRdpWidth() {
        return rdpWidth;
    }
    
    public void setRdpWidth(int rdpWidth) {
        this.rdpWidth = rdpWidth;
    }
    
    public int getRdpHeight() {
        return rdpHeight;
    }
    
    public void setRdpHeight(int rdpHeight) {
        this.rdpHeight = rdpHeight;
    }
    
    public boolean isRdpFullscreen() {
        return rdpFullscreen;
    }
    
    public void setRdpFullscreen(boolean rdpFullscreen) {
        this.rdpFullscreen = rdpFullscreen;
    }
    
    public boolean hasPrivateKey() {
        return privateKey != null && !privateKey.isEmpty();
    }
    
    public boolean hasProxy() {
        return proxyId != null && !proxyId.isEmpty();
    }
    
    public String getTerminalType() {
        return terminalType;
    }
    
    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }
    
    public int getTerminalCols() {
        return terminalCols;
    }
    
    public void setTerminalCols(int terminalCols) {
        this.terminalCols = terminalCols;
    }
    
    public int getTerminalRows() {
        return terminalRows;
    }
    
    public void setTerminalRows(int terminalRows) {
        this.terminalRows = terminalRows;
    }
    
    public boolean isEnableCompression() {
        return enableCompression;
    }
    
    public void setEnableCompression(boolean enableCompression) {
        this.enableCompression = enableCompression;
    }
    
    @Override
    public String toString() {
        return name != null ? name : host;
    }
    
    // Alias methods for compatibility
    public String getKeyPath() { return privateKey; }
    public void setKeyPath(String keyPath) { this.privateKey = keyPath; }
    public String getKeyPassphrase() { return passphrase; }
    public String getUser() { return userName; }
    public String getUsername() { return userName; }
    public void setUsername(String username) { this.userName = username; }
    public String getFolderId() { return parentId; }
    public void setEncryptedPassword(String encPassword) { this.password = encPassword; }
    
    private boolean rememberPassword = true;
    public boolean isRememberPassword() { return rememberPassword; }
    public void setRememberPassword(boolean rememberPassword) { this.rememberPassword = rememberPassword; }
    
    public static final int TYPE_RDP = 3;
    
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
