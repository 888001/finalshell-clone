package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 加载框架
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Nav_Loading_UI_DeepAnalysis.md - LoadingFrame
 */
public class LoadingFrame extends JFrame {
    
    private LoadingPanel loadingPanel;
    
    public LoadingFrame() {
        setUndecorated(true);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));
        
        loadingPanel = new LoadingPanel();
        add(loadingPanel);
        pack();
    }
    
    /**
     * 显示加载状态
     */
    public void showLoading(String message) {
        loadingPanel.setMessage(message);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * 显示在指定窗口中心
     */
    public void showLoading(String message, Window parent) {
        loadingPanel.setMessage(message);
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    /**
     * 隐藏加载状态
     */
    public void hideLoading() {
        setVisible(false);
    }
    
    /**
     * 更新消息
     */
    public void updateMessage(String message) {
        loadingPanel.setMessage(message);
    }
    
    public LoadingPanel getLoadingPanel() { return loadingPanel; }
}
