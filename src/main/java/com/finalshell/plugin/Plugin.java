package com.finalshell.plugin;

import javax.swing.*;

/**
 * 插件接口
 */
public interface Plugin {
    
    /**
     * 获取插件ID (唯一标识)
     */
    String getId();
    
    /**
     * 获取插件名称
     */
    String getName();
    
    /**
     * 获取插件版本
     */
    String getVersion();
    
    /**
     * 获取插件作者
     */
    String getAuthor();
    
    /**
     * 获取插件描述
     */
    String getDescription();
    
    /**
     * 插件初始化
     */
    void init(PluginContext context);
    
    /**
     * 插件启用
     */
    void enable();
    
    /**
     * 插件禁用
     */
    void disable();
    
    /**
     * 插件销毁
     */
    void destroy();
    
    /**
     * 获取插件菜单项 (可选)
     */
    default JMenuItem getMenuItem() {
        return null;
    }
    
    /**
     * 获取插件工具栏按钮 (可选)
     */
    default JButton getToolbarButton() {
        return null;
    }
    
    /**
     * 获取插件面板 (可选)
     */
    default JPanel getPanel() {
        return null;
    }
    
    /**
     * 获取插件设置面板 (可选)
     */
    default JPanel getSettingsPanel() {
        return null;
    }
}
