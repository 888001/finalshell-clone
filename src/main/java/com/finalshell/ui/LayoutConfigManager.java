package com.finalshell.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.finalshell.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * 布局配置管理器 - 对齐原版myssh复杂实现
 * 
 * Based on analysis of myssh/ui/LayoutConfigManager.java (82行)
 * 管理多种标签页的布局配置
 */
public class LayoutConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(LayoutConfigManager.class);
    private static LayoutConfigManager instance;
    
    // 各类标签页的布局配置 - 对齐原版myssh
    private LayoutConfig tabTerminal = new LayoutConfig();       // 终端标签
    private LayoutConfig tabNewTab = new LayoutConfig();         // 新标签页
    private LayoutConfig tabTaskTab = new LayoutConfig();        // 任务标签页  
    private LayoutConfig tabNetManagerTab = new LayoutConfig();  // 网络管理标签页
    private LayoutConfig tabHostDetectTab = new LayoutConfig();  // 主机检测标签页
    private LayoutConfig tabSysInfoTab = new LayoutConfig();     // 系统信息标签页
    
    private LayoutConfigManager() {
        // 初始化各标签页配置 - 对齐原版myssh逻辑
        initTabConfigs();
        loadLayouts();
    }
    
    /**
     * 初始化各标签页配置 - 对齐原版myssh
     */
    private void initTabConfigs() {
        // 终端标签配置
        tabTerminal.setVisible(true);
        tabTerminal.setWidth(250);
        
        // 新标签页配置
        tabNewTab.setVisible(false);
        
        // 任务标签页配置
        tabTaskTab.setVisible(true);
        tabTaskTab.setWidth(300);
        
        // 网络管理标签页配置
        tabNetManagerTab.setVisible(true);
        tabNetManagerTab.setWidth(300);
        
        // 主机检测标签页配置
        tabHostDetectTab.setVisible(true);
        tabHostDetectTab.setWidth(300);
        
        // 系统信息标签页配置
        tabSysInfoTab.setVisible(false);
    }
    
    public static synchronized LayoutConfigManager getInstance() {
        if (instance == null) {
            instance = new LayoutConfigManager();
        }
        return instance;
    }
    
    private void loadLayouts() {
        try {
            File layoutFile = new File(ConfigManager.getInstance().getConfigDir(), "layout.json");
            if (layoutFile.exists()) {
                String content = new String(Files.readAllBytes(layoutFile.toPath()), StandardCharsets.UTF_8);
                JSONObject json = JSON.parseObject(content);
                
                // 加载各标签页配置 - 对齐原版myssh
                loadTabConfig(json.getJSONObject("tab_terminal"), tabTerminal);
                loadTabConfig(json.getJSONObject("tab_newtab"), tabNewTab);
                loadTabConfig(json.getJSONObject("tab_tasktab"), tabTaskTab);
                loadTabConfig(json.getJSONObject("tab_net_mananagertab"), tabNetManagerTab);
                loadTabConfig(json.getJSONObject("tab_host_detect_tab"), tabHostDetectTab);
                
                logger.info("已加载布局配置");
            }
        } catch (Exception e) {
            logger.error("加载布局配置失败", e);
        }
    }
    
    /**
     * 加载单个标签配置
     */
    private void loadTabConfig(JSONObject json, LayoutConfig config) {
        if (json != null && config != null) {
            config.setVisible(json.getBooleanValue("visible"));
            config.setWidth(json.getIntValue("width"));
        }
    }
    
    /**
     * 保存布局配置到JSON文件 - 对齐原版myssh
     */
    public void saveLayouts() {
        try {
            JSONObject json = new JSONObject();
            
            // 保存各标签页配置 - 对齐原版myssh
            json.put("tab_terminal", tabTerminal.toJSON());
            json.put("tab_newtab", tabNewTab.toJSON());
            json.put("tab_tasktab", tabTaskTab.toJSON());
            json.put("tab_net_mananagertab", tabNetManagerTab.toJSON());
            json.put("tab_host_detect_tab", tabHostDetectTab.toJSON());
            
            File layoutFile = new File(ConfigManager.getInstance().getConfigDir(), "layout.json");
            Files.write(layoutFile.toPath(), JSON.toJSONString(json, true).getBytes(StandardCharsets.UTF_8));
            logger.info("已保存布局配置");
        } catch (Exception e) {
            logger.error("保存布局配置失败", e);
        }
    }
    
    /**
     * 从JSON加载配置 - 对齐原版myssh
     */
    public void loadFromJSON(JSONObject json) {
        loadTabConfig(json.getJSONObject("tab_terminal"), tabTerminal);
        loadTabConfig(json.getJSONObject("tab_newtab"), tabNewTab);
        loadTabConfig(json.getJSONObject("tab_tasktab"), tabTaskTab);
        loadTabConfig(json.getJSONObject("tab_net_mananagertab"), tabNetManagerTab);
        loadTabConfig(json.getJSONObject("tab_host_detect_tab"), tabHostDetectTab);
    }
    
    /**
     * 导出到JSON - 对齐原版myssh
     */
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("tab_terminal", tabTerminal.toJSON());
        json.put("tab_newtab", tabNewTab.toJSON());
        json.put("tab_tasktab", tabTaskTab.toJSON());
        json.put("tab_net_mananagertab", tabNetManagerTab.toJSON());
        json.put("tab_host_detect_tab", tabHostDetectTab.toJSON());
        return json;
    }
    
    // Getter方法 - 对齐原版myssh的所有布局配置访问
    public LayoutConfig getTabTerminal() {
        return tabTerminal;
    }
    
    public LayoutConfig getTabNewTab() {
        return tabNewTab;
    }
    
    public LayoutConfig getTabTaskTab() {
        return tabTaskTab;
    }
    
    public LayoutConfig getTabNetManagerTab() {
        return tabNetManagerTab;
    }
    
    public LayoutConfig getTabHostDetectTab() {
        return tabHostDetectTab;
    }
    
    public LayoutConfig getTabSysInfoTab() {
        return tabSysInfoTab;
    }
    
    public void setTabSysInfoTab(LayoutConfig tabSysInfoTab) {
        this.tabSysInfoTab = tabSysInfoTab;
    }
}
