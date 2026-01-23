package com.finalshell.telnet;

import java.io.Serializable;
import java.util.UUID;

/**
 * Telnet Connection Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TelnetConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String host;
    private int port = 23;
    private String charset = "UTF-8";
    
    // Authentication
    private String username;
    private String password;
    private boolean autoLogin = false;
    
    // Terminal settings
    private String terminalType = "xterm";
    private int terminalWidth = 80;
    private int terminalHeight = 24;
    
    // Connection settings
    private int connectionTimeout = 30000;
    private int readTimeout = 0;
    private boolean localEcho = false;
    
    public TelnetConfig() {
        this.id = UUID.randomUUID().toString();
    }
    
    public TelnetConfig(String name, String host, int port) {
        this();
        this.name = name;
        this.host = host;
        this.port = port;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public boolean isAutoLogin() { return autoLogin; }
    public void setAutoLogin(boolean autoLogin) { this.autoLogin = autoLogin; }
    
    public String getTerminalType() { return terminalType; }
    public void setTerminalType(String terminalType) { this.terminalType = terminalType; }
    
    public int getTerminalWidth() { return terminalWidth; }
    public void setTerminalWidth(int terminalWidth) { this.terminalWidth = terminalWidth; }
    
    public int getTerminalHeight() { return terminalHeight; }
    public void setTerminalHeight(int terminalHeight) { this.terminalHeight = terminalHeight; }
    
    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    
    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) { this.readTimeout = readTimeout; }
    
    public boolean isLocalEcho() { return localEcho; }
    public void setLocalEcho(boolean localEcho) { this.localEcho = localEcho; }
    
    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
