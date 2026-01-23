package com.finalshell.event;

/**
 * SSH会话监听器接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Tab_Log_Misc_Analysis.md
 */
public interface SessionListener {
    
    /**
     * 连接成功
     */
    void onConnected(SessionEvent event);
    
    /**
     * 连接断开
     */
    void onDisconnected(SessionEvent event);
    
    /**
     * 认证失败
     */
    void onAuthFailed(SessionEvent event);
    
    /**
     * 连接超时
     */
    void onTimeout(SessionEvent event);
    
    /**
     * 发生错误
     */
    void onError(SessionEvent event);
    
    /**
     * 适配器类 - 提供空实现
     */
    class Adapter implements SessionListener {
        @Override
        public void onConnected(SessionEvent event) {}
        
        @Override
        public void onDisconnected(SessionEvent event) {}
        
        @Override
        public void onAuthFailed(SessionEvent event) {}
        
        @Override
        public void onTimeout(SessionEvent event) {}
        
        @Override
        public void onError(SessionEvent event) {}
    }
}
