package com.finalshell.ui;

import com.finalshell.config.ConfigManager;

import java.util.*;

/**
 * 布局配置管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - LayoutConfigManager
 */
public class LayoutConfigManager {
    
    private static LayoutConfigManager instance;
    private Map<String, LayoutConfig> layouts = new HashMap<>();
    private String currentLayout = "default";
    
    private LayoutConfigManager() {
        loadLayouts();
    }
    
    public static synchronized LayoutConfigManager getInstance() {
        if (instance == null) {
            instance = new LayoutConfigManager();
        }
        return instance;
    }
    
    private void loadLayouts() {
        // 加载默认布局
        LayoutConfig defaultLayout = new LayoutConfig();
        defaultLayout.setName("default");
        defaultLayout.setShowTree(true);
        defaultLayout.setShowStatusBar(true);
        defaultLayout.setTreeWidth(250);
        layouts.put("default", defaultLayout);
        
        // 加载保存的布局配置
        try {
            ConfigManager configManager = ConfigManager.getInstance();
            // TODO: 从配置文件加载布局
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void saveLayouts() {
        try {
            ConfigManager configManager = ConfigManager.getInstance();
            // TODO: 保存布局到配置文件
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public LayoutConfig getLayout(String name) {
        return layouts.get(name);
    }
    
    public LayoutConfig getCurrentLayout() {
        return layouts.get(currentLayout);
    }
    
    public void setCurrentLayout(String name) {
        if (layouts.containsKey(name)) {
            this.currentLayout = name;
        }
    }
    
    public void addLayout(LayoutConfig layout) {
        layouts.put(layout.getName(), layout);
        saveLayouts();
    }
    
    public void removeLayout(String name) {
        if (!"default".equals(name)) {
            layouts.remove(name);
            saveLayouts();
        }
    }
    
    public List<String> getLayoutNames() {
        return new ArrayList<>(layouts.keySet());
    }
}
