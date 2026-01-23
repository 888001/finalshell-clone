package com.finalshell.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端配置
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ClientConfiguration {
    
    private static ClientConfiguration instance;
    
    private String userAgent;
    private int connectionTimeout = 30000;
    private int readTimeout = 60000;
    private boolean followRedirects = true;
    private Map<String, String> defaultHeaders;
    private ProxyConfig proxyConfig;
    
    private ClientConfiguration() {
        this.defaultHeaders = new HashMap<>();
        this.userAgent = "FinalShell/3.8.3";
    }
    
    public static synchronized ClientConfiguration getInstance() {
        if (instance == null) {
            instance = new ClientConfiguration();
        }
        return instance;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getReadTimeout() {
        return readTimeout;
    }
    
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public boolean isFollowRedirects() {
        return followRedirects;
    }
    
    public void setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }
    
    public void setHeader(String name, String value) {
        defaultHeaders.put(name, value);
    }
    
    public String getHeader(String name) {
        return defaultHeaders.get(name);
    }
    
    public Map<String, String> getDefaultHeaders() {
        return new HashMap<>(defaultHeaders);
    }
    
    public ProxyConfig getProxyConfig() {
        return proxyConfig;
    }
    
    public void setProxyConfig(ProxyConfig proxyConfig) {
        this.proxyConfig = proxyConfig;
    }
    
    public boolean hasProxy() {
        return proxyConfig != null && proxyConfig.isEnabled();
    }
}
