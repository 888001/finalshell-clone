package com.finalshell.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.finalshell.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * 布局配置管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - LayoutConfigManager
 */
public class LayoutConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(LayoutConfigManager.class);
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
            File layoutFile = new File(ConfigManager.getInstance().getConfigDir(), "layouts.json");
            if (layoutFile.exists()) {
                String content = new String(Files.readAllBytes(layoutFile.toPath()), StandardCharsets.UTF_8);
                JSONArray jsonArray = JSON.parseArray(content);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    LayoutConfig layout = new LayoutConfig();
                    layout.setName(obj.getString("name"));
                    layout.setShowTree(obj.getBooleanValue("showTree"));
                    layout.setShowStatusBar(obj.getBooleanValue("showStatusBar"));
                    layout.setTreeWidth(obj.getIntValue("treeWidth"));
                    layouts.put(layout.getName(), layout);
                }
                logger.info("加载布局配置: {} 个", jsonArray.size());
            }
        } catch (Exception e) {
            logger.error("加载布局配置失败", e);
        }
    }
    
    public void saveLayouts() {
        try {
            JSONArray jsonArray = new JSONArray();
            for (LayoutConfig layout : layouts.values()) {
                JSONObject obj = new JSONObject();
                obj.put("name", layout.getName());
                obj.put("showTree", layout.isShowTree());
                obj.put("showStatusBar", layout.isShowStatusBar());
                obj.put("treeWidth", layout.getTreeWidth());
                jsonArray.add(obj);
            }
            File layoutFile = new File(ConfigManager.getInstance().getConfigDir(), "layouts.json");
            Files.write(layoutFile.toPath(), JSON.toJSONString(jsonArray, true).getBytes(StandardCharsets.UTF_8));
            logger.info("保存布局配置: {} 个", layouts.size());
        } catch (Exception e) {
            logger.error("保存布局配置失败", e);
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
