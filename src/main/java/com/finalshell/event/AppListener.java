package com.finalshell.event;

/**
 * 应用监听器接口
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public interface AppListener {
    
    void onApplicationStart();
    
    void onApplicationExit();
    
    void onConnectionOpened(String connectionId);
    
    void onConnectionClosed(String connectionId);
    
    void onThemeChanged(String themeName);
    
    void onLanguageChanged(String language);
    
    void onConfigChanged(String configKey, Object value);
}
