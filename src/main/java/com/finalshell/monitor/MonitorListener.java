package com.finalshell.monitor;

/**
 * Monitor Listener - Callback interface for monitoring events
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public interface MonitorListener {
    
    /**
     * Called when monitoring data is updated
     */
    void onMonitorUpdate(MonitorData data);
    
    /**
     * Called when monitoring starts
     */
    default void onMonitorStart() {}
    
    /**
     * Called when monitoring stops
     */
    default void onMonitorStop() {}
    
    /**
     * Called when an error occurs during monitoring
     */
    default void onMonitorError(Exception e) {}
}
