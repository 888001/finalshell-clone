package com.finalshell.ui;

/**
 * 应用事件监听器接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md - AppListener
 */
public interface AppListener {
    
    /**
     * 应用事件回调
     * @param event 事件对象
     */
    void onAppEvent(AppEvent event);
}
