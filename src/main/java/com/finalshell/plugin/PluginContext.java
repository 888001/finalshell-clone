package com.finalshell.plugin;

import com.finalshell.config.ConfigManager;
import com.finalshell.ssh.SSHSession;
import com.finalshell.ssh.SSHSessionManager;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插件上下文 - 提供插件访问应用功能的接口
 */
public class PluginContext {
    private final JFrame mainWindow;
    private final ConfigManager configManager;
    private final SSHSessionManager sessionManager;
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    
    public PluginContext(JFrame mainWindow, ConfigManager configManager, 
                        SSHSessionManager sessionManager) {
        this.mainWindow = mainWindow;
        this.configManager = configManager;
        this.sessionManager = sessionManager;
    }
    
    /**
     * 获取主窗口
     */
    public JFrame getMainWindow() {
        return mainWindow;
    }
    
    /**
     * 获取配置管理器
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    /**
     * 获取会话管理器
     */
    public SSHSessionManager getSessionManager() {
        return sessionManager;
    }
    
    /**
     * 获取当前活动的SSH会话
     */
    public SSHSession getActiveSession() {
        return sessionManager.getActiveSession();
    }
    
    /**
     * 设置属性
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * 获取属性
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * 移除属性
     */
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    /**
     * 显示消息
     */
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(mainWindow, message);
    }
    
    /**
     * 显示错误消息
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(mainWindow, message, "错误", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * 显示确认对话框
     */
    public boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(mainWindow, message, "确认", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    /**
     * 显示输入对话框
     */
    public String input(String message) {
        return JOptionPane.showInputDialog(mainWindow, message);
    }
    
    /**
     * 添加菜单项
     */
    public void addMenuItem(JMenu menu, JMenuItem item) {
        if (menu != null && item != null) {
            SwingUtilities.invokeLater(() -> menu.add(item));
        }
    }
    
    /**
     * 添加工具栏按钮
     */
    public void addToolbarButton(JToolBar toolbar, JButton button) {
        if (toolbar != null && button != null) {
            SwingUtilities.invokeLater(() -> toolbar.add(button));
        }
    }
}
