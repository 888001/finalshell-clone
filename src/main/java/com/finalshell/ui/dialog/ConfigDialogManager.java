package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * 配置对话框管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - ConfigDialogManager
 */
public class ConfigDialogManager {
    
    private static ConfigDialogManager instance;
    private Map<String, JDialog> openDialogs = new HashMap<>();
    private Frame mainFrame;
    
    private ConfigDialogManager() {}
    
    public static synchronized ConfigDialogManager getInstance() {
        if (instance == null) {
            instance = new ConfigDialogManager();
        }
        return instance;
    }
    
    public void setMainFrame(Frame frame) {
        this.mainFrame = frame;
    }
    
    public Frame getMainFrame() {
        return mainFrame;
    }
    
    public void registerDialog(String id, JDialog dialog) {
        openDialogs.put(id, dialog);
    }
    
    public void unregisterDialog(String id) {
        openDialogs.remove(id);
    }
    
    public JDialog getDialog(String id) {
        return openDialogs.get(id);
    }
    
    public boolean isDialogOpen(String id) {
        JDialog dialog = openDialogs.get(id);
        return dialog != null && dialog.isVisible();
    }
    
    public void showDialog(String id) {
        JDialog dialog = openDialogs.get(id);
        if (dialog != null) {
            dialog.setVisible(true);
            dialog.toFront();
        }
    }
    
    public void closeDialog(String id) {
        JDialog dialog = openDialogs.get(id);
        if (dialog != null) {
            dialog.dispose();
            openDialogs.remove(id);
        }
    }
    
    public void closeAllDialogs() {
        for (JDialog dialog : new ArrayList<>(openDialogs.values())) {
            dialog.dispose();
        }
        openDialogs.clear();
    }
    
    public Set<String> getOpenDialogIds() {
        return new HashSet<>(openDialogs.keySet());
    }
    
    public int getOpenDialogCount() {
        return openDialogs.size();
    }
    
    public void bringToFront(String id) {
        JDialog dialog = openDialogs.get(id);
        if (dialog != null && dialog.isVisible()) {
            dialog.toFront();
            dialog.requestFocus();
        }
    }
    
    public void minimizeAll() {
        for (JDialog dialog : openDialogs.values()) {
            if (dialog.isVisible()) {
                dialog.setVisible(false);
            }
        }
    }
    
    public void restoreAll() {
        for (JDialog dialog : openDialogs.values()) {
            dialog.setVisible(true);
        }
    }
}
