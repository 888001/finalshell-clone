package com.finalshell.ui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置对话框管理器 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/ui/ConfigDialogmanage.java
 * 管理所有配置对话框的创建、显示和销毁
 */
public class ConfigDialogManager {
    
    private static ConfigDialogManager instance;
    private final Map<String, JFrame> dialogMap = new HashMap<>();
    
    private ConfigDialogManager() {}
    
    public static synchronized ConfigDialogManager getInstance() {
        if (instance == null) {
            instance = new ConfigDialogManager();
        }
        return instance;
    }
    
    /**
     * 注册配置对话框
     */
    public void registerDialog(String id, JFrame dialog) {
        dialogMap.put(id, dialog);
    }
    
    /**
     * 获取配置对话框
     */
    public JFrame getDialog(String id) {
        return dialogMap.get(id);
    }
    
    /**
     * 移除配置对话框
     */
    public void removeDialog(String id) {
        JFrame dialog = dialogMap.remove(id);
        if (dialog != null) {
            dialog.dispose();
        }
    }
    
    /**
     * 检查对话框是否存在
     */
    public boolean containsDialog(String id) {
        return dialogMap.containsKey(id);
    }
    
    /**
     * 显示指定的配置对话框
     */
    public void showDialog(String id) {
        JFrame dialog = dialogMap.get(id);
        if (dialog != null) {
            dialog.setVisible(true);
            dialog.toFront();
        }
    }
    
    /**
     * 隐藏指定的配置对话框
     */
    public void hideDialog(String id) {
        JFrame dialog = dialogMap.get(id);
        if (dialog != null) {
            dialog.setVisible(false);
        }
    }
    
    /**
     * 关闭所有配置对话框
     */
    public void closeAllDialogs() {
        for (JFrame dialog : dialogMap.values()) {
            dialog.dispose();
        }
        dialogMap.clear();
    }
    
    /**
     * 获取所有对话框ID
     */
    public java.util.Set<String> getDialogIds() {
        return new java.util.HashSet<>(dialogMap.keySet());
    }
}
