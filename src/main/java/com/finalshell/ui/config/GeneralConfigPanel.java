package com.finalshell.ui.config;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.util.OSDetector;

import javax.swing.*;
import java.awt.*;

/**
 * 常规配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: GlobalConfig_UI_DeepAnalysis.md - GenneralConfigPanel
 */
public class GeneralConfigPanel extends ConfigPanel {
    
    private JCheckBox autoSelectTab;
    private JCheckBox closeToTray;
    private JCheckBox confirmClose;
    private JCheckBox autoStart;
    private JComboBox<String> languageCombo;
    
    public GeneralConfigPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 标签设置
        JPanel tabPanel = createSection("标签页");
        autoSelectTab = new JCheckBox("自动选中新标签");
        tabPanel.add(autoSelectTab);
        add(tabPanel);
        
        add(Box.createVerticalStrut(10));
        
        // 窗口设置
        JPanel windowPanel = createSection("窗口");
        confirmClose = new JCheckBox("关闭窗口前确认");
        windowPanel.add(confirmClose);
        
        if (OSDetector.isWindows() || OSDetector.isMac()) {
            closeToTray = new JCheckBox("关闭后最小化到托盘");
            windowPanel.add(closeToTray);
        }
        
        if (OSDetector.isWindows()) {
            autoStart = new JCheckBox("开机启动");
            windowPanel.add(autoStart);
        }
        add(windowPanel);
        
        add(Box.createVerticalStrut(10));
        
        // 语言设置
        JPanel langPanel = createSection("语言");
        JPanel langRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        langRow.setOpaque(false);
        langRow.add(new JLabel("界面语言:"));
        languageCombo = new JComboBox<>(new String[]{"简体中文", "English", "日本語"});
        langRow.add(languageCombo);
        langPanel.add(langRow);
        add(langPanel);
        
        add(Box.createVerticalGlue());
    }
    
    private JPanel createSection(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }
    
    @Override
    public void apply() {
        AppConfig config = ConfigManager.getInstance().getAppConfig();
        if (config != null) {
            config.setAutoSelectTab(autoSelectTab.isSelected());
            config.setConfirmExit(confirmClose.isSelected());
            if (closeToTray != null) {
                config.setMinimizeToTray(closeToTray.isSelected());
            }
            if (autoStart != null) {
                config.setAutoStart(autoStart.isSelected());
            }
            ConfigManager.getInstance().saveConfig();
        }
    }
    
    @Override
    public void reset() {
        AppConfig config = ConfigManager.getInstance().getAppConfig();
        if (config != null) {
            autoSelectTab.setSelected(config.isAutoSelectTab());
            confirmClose.setSelected(config.isConfirmExit());
            if (closeToTray != null) {
                closeToTray.setSelected(config.isMinimizeToTray());
            }
            if (autoStart != null) {
                autoStart.setSelected(config.isAutoStart());
            }
        }
    }
}
