package com.finalshell.ui.config;

import javax.swing.*;

/**
 * 配置面板基类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: GlobalConfig_UI_DeepAnalysis.md
 */
public abstract class ConfigPanel extends JPanel {
    
    /**
     * 应用配置
     */
    public abstract void apply();
    
    /**
     * 重置配置
     */
    public abstract void reset();
    
    /**
     * 验证配置
     */
    public boolean validate() {
        return true;
    }
}
