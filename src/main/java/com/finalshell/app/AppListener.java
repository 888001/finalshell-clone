package com.finalshell.app;

/**
 * 应用程序事件监听器接口
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public interface AppListener {
    
    /**
     * 当应用程序事件发生时调用
     * 
     * @param event 应用程序事件
     */
    void onAppEvent(AppEvent event);
}
