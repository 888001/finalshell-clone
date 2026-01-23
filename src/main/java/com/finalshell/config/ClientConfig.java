package com.finalshell.config;

import java.io.Serializable;

/**
 * 客户端配置
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ClientConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String clientId;
    private String clientName;
    private String version;
    private String language;
    private String theme;
    private boolean autoUpdate;
    private boolean showWelcome;
    private int maxConnections;
    private int sessionTimeout;
    
    public ClientConfig() {
        this.language = "zh_CN";
        this.theme = "default";
        this.autoUpdate = true;
        this.showWelcome = true;
        this.maxConnections = 10;
        this.sessionTimeout = 30;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }
    
    public boolean isShowWelcome() {
        return showWelcome;
    }
    
    public void setShowWelcome(boolean showWelcome) {
        this.showWelcome = showWelcome;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getSessionTimeout() {
        return sessionTimeout;
    }
    
    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }
}
