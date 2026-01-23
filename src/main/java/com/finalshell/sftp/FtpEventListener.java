package com.finalshell.sftp;

/**
 * FTP事件监听器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - FtpEventListener
 */
public interface FtpEventListener {
    
    void onConnected();
    
    void onDisconnected();
    
    void onDirectoryChanged(String path);
    
    void onFileListUpdated();
    
    void onError(Exception e);
}
