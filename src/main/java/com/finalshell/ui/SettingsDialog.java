package com.finalshell.ui;

import com.finalshell.config.AppConfig;
import com.finalshell.config.ConfigManager;
import com.finalshell.hotkey.HotkeyDialog;
import com.finalshell.i18n.I18n;
import com.finalshell.i18n.LanguageDialog;
import com.finalshell.layout.LayoutDialog;
import com.finalshell.plugin.PluginDialog;
import com.finalshell.theme.ThemeDialog;
import com.finalshell.theme.ThemeManager;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

/**
 * 设置对话框
 */
public class SettingsDialog extends JDialog {
    private final ConfigManager configManager;
    private AppConfig appConfig;
    
    private JTabbedPane tabbedPane;
    
    // 通用设置
    private JCheckBox autoReconnectCheck;
    private JSpinner reconnectDelaySpinner;
    private JCheckBox confirmExitCheck;
    private JCheckBox minimizeToTrayCheck;
    private JCheckBox startMinimizedCheck;
    
    // 终端设置
    private JSpinner terminalRowsSpinner;
    private JSpinner terminalColsSpinner;
    private JSpinner scrollbackSpinner;
    private JComboBox<String> fontCombo;
    private JSpinner fontSizeSpinner;
    private JCheckBox cursorBlinkCheck;
    private JCheckBox audibleBellCheck;
    
    // 传输设置
    private JSpinner maxConcurrentSpinner;
    private JSpinner bufferSizeSpinner;
    private JCheckBox confirmOverwriteCheck;
    private JCheckBox preserveTimestampCheck;
    
    // 安全设置
    private JCheckBox rememberPasswordCheck;
    private JCheckBox autoLockCheck;
    private JSpinner autoLockTimeSpinner;
    private JPasswordField masterPasswordField;
    
    public SettingsDialog(Window owner, ConfigManager configManager) {
        super(owner, "设置", ModalityType.APPLICATION_MODAL);
        this.configManager = configManager;
        this.appConfig = configManager.getAppConfig();
        
        setSize(600, 500);
        setLocationRelativeTo(owner);
        
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        
        // 通用设置
        tabbedPane.addTab("通用", createGeneralPanel());
        
        // 终端设置
        tabbedPane.addTab("终端", createTerminalPanel());
        
        // 传输设置
        tabbedPane.addTab("传输", createTransferPanel());
        
        // 安全设置
        tabbedPane.addTab("安全", createSecurityPanel());
        
        // 外观设置
        tabbedPane.addTab("外观", createAppearancePanel());
        
        // 扩展设置
        tabbedPane.addTab("扩展", createExtensionPanel());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = new JButton("确定");
        JButton cancelBtn = new JButton("取消");
        JButton applyBtn = new JButton("应用");
        
        okBtn.addActionListener(e -> {
            saveSettings();
            dispose();
        });
        
        cancelBtn.addActionListener(e -> dispose());
        
        applyBtn.addActionListener(e -> saveSettings());
        
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        buttonPanel.add(applyBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // 自动重连
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        autoReconnectCheck = new JCheckBox("断开后自动重连");
        panel.add(autoReconnectCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("重连延迟 (秒):"), gbc);
        gbc.gridx = 1;
        reconnectDelaySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        panel.add(reconnectDelaySpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        confirmExitCheck = new JCheckBox("退出时确认");
        panel.add(confirmExitCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        minimizeToTrayCheck = new JCheckBox("最小化到系统托盘");
        panel.add(minimizeToTrayCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        startMinimizedCheck = new JCheckBox("启动时最小化");
        panel.add(startMinimizedCheck, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel createTerminalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // 终端大小
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("终端行数:"), gbc);
        gbc.gridx = 1;
        terminalRowsSpinner = new JSpinner(new SpinnerNumberModel(24, 10, 100, 1));
        panel.add(terminalRowsSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("终端列数:"), gbc);
        gbc.gridx = 1;
        terminalColsSpinner = new JSpinner(new SpinnerNumberModel(80, 40, 300, 1));
        panel.add(terminalColsSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("滚动缓冲区:"), gbc);
        gbc.gridx = 1;
        scrollbackSpinner = new JSpinner(new SpinnerNumberModel(10000, 1000, 100000, 1000));
        panel.add(scrollbackSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("字体:"), gbc);
        gbc.gridx = 1;
        String[] fonts = {"Consolas", "Courier New", "Monospace", "DejaVu Sans Mono"};
        fontCombo = new JComboBox<>(fonts);
        panel.add(fontCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("字体大小:"), gbc);
        gbc.gridx = 1;
        fontSizeSpinner = new JSpinner(new SpinnerNumberModel(14, 8, 32, 1));
        panel.add(fontSizeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        cursorBlinkCheck = new JCheckBox("光标闪烁");
        panel.add(cursorBlinkCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        audibleBellCheck = new JCheckBox("声音提示");
        panel.add(audibleBellCheck, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("最大并发传输:"), gbc);
        gbc.gridx = 1;
        maxConcurrentSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        panel.add(maxConcurrentSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("传输缓冲区 (KB):"), gbc);
        gbc.gridx = 1;
        bufferSizeSpinner = new JSpinner(new SpinnerNumberModel(32, 4, 256, 4));
        panel.add(bufferSizeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        confirmOverwriteCheck = new JCheckBox("覆盖前确认");
        panel.add(confirmOverwriteCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        preserveTimestampCheck = new JCheckBox("保留文件时间戳");
        panel.add(preserveTimestampCheck, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        rememberPasswordCheck = new JCheckBox("记住密码");
        panel.add(rememberPasswordCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        autoLockCheck = new JCheckBox("自动锁定");
        panel.add(autoLockCheck, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel("锁定时间 (分钟):"), gbc);
        gbc.gridx = 1;
        autoLockTimeSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        panel.add(autoLockTimeSpinner, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("主密码:"), gbc);
        gbc.gridx = 1;
        masterPasswordField = new JPasswordField(20);
        panel.add(masterPasswordField, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel createAppearancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // 主题
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("主题:"), gbc);
        gbc.gridx = 1;
        JButton themeBtn = new JButton("选择主题...");
        themeBtn.addActionListener(e -> ThemeDialog.show(this));
        panel.add(themeBtn, gbc);
        
        row++;
        // 语言
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("语言:"), gbc);
        gbc.gridx = 1;
        JButton langBtn = new JButton("选择语言...");
        langBtn.addActionListener(e -> LanguageDialog.showDialog(this));
        panel.add(langBtn, gbc);
        
        row++;
        // 布局
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("布局:"), gbc);
        gbc.gridx = 1;
        JButton layoutBtn = new JButton("管理布局...");
        layoutBtn.addActionListener(e -> {
            Window owner = SwingUtilities.getWindowAncestor(this);
            if (owner instanceof JFrame) {
                LayoutDialog.show((JFrame) owner);
            }
        });
        panel.add(layoutBtn, gbc);
        
        row++;
        // 热键
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("热键:"), gbc);
        gbc.gridx = 1;
        JButton hotkeyBtn = new JButton("设置热键...");
        hotkeyBtn.addActionListener(e -> HotkeyDialog.show(this));
        panel.add(hotkeyBtn, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private JPanel createExtensionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // 插件
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("插件:"), gbc);
        gbc.gridx = 1;
        JButton pluginBtn = new JButton("管理插件...");
        pluginBtn.addActionListener(e -> PluginDialog.show(this));
        panel.add(pluginBtn, gbc);
        
        row++;
        // 关于信息
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("关于"));
        infoPanel.add(new JLabel("FinalShell Clone"));
        infoPanel.add(new JLabel("版本: 1.0.0"));
        infoPanel.add(new JLabel("基于: FinalShell 3.8.3 分析"));
        infoPanel.add(new JLabel("Java: " + System.getProperty("java.version")));
        panel.add(infoPanel, gbc);
        
        // 填充空白
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 1.0;
        panel.add(new JLabel(), gbc);
        
        return panel;
    }
    
    private void loadSettings() {
        if (appConfig == null) return;
        
        // 通用设置
        autoReconnectCheck.setSelected(appConfig.isAutoReconnect());
        reconnectDelaySpinner.setValue(appConfig.getReconnectDelay());
        confirmExitCheck.setSelected(appConfig.isConfirmExit());
        minimizeToTrayCheck.setSelected(appConfig.isMinimizeToTray());
        startMinimizedCheck.setSelected(appConfig.isStartMinimized());
        
        // 终端设置
        terminalRowsSpinner.setValue(appConfig.getTerminalRows());
        terminalColsSpinner.setValue(appConfig.getTerminalCols());
        scrollbackSpinner.setValue(appConfig.getScrollbackLines());
        fontCombo.setSelectedItem(appConfig.getTerminalFont());
        fontSizeSpinner.setValue(appConfig.getTerminalFontSize());
        cursorBlinkCheck.setSelected(appConfig.isCursorBlink());
        audibleBellCheck.setSelected(appConfig.isAudibleBell());
        
        // 传输设置
        maxConcurrentSpinner.setValue(appConfig.getMaxConcurrentTransfers());
        bufferSizeSpinner.setValue(appConfig.getTransferBufferSize());
        confirmOverwriteCheck.setSelected(appConfig.isConfirmOverwrite());
        preserveTimestampCheck.setSelected(appConfig.isPreserveTimestamp());
        
        // 安全设置
        rememberPasswordCheck.setSelected(appConfig.isRememberPassword());
        autoLockCheck.setSelected(appConfig.isAutoLock());
        autoLockTimeSpinner.setValue(appConfig.getAutoLockTime());
    }
    
    private void saveSettings() {
        if (appConfig == null) return;
        
        // 通用设置
        appConfig.setAutoReconnect(autoReconnectCheck.isSelected());
        appConfig.setReconnectDelay((Integer) reconnectDelaySpinner.getValue());
        appConfig.setConfirmExit(confirmExitCheck.isSelected());
        appConfig.setMinimizeToTray(minimizeToTrayCheck.isSelected());
        appConfig.setStartMinimized(startMinimizedCheck.isSelected());
        
        // 终端设置
        appConfig.setTerminalRows((Integer) terminalRowsSpinner.getValue());
        appConfig.setTerminalCols((Integer) terminalColsSpinner.getValue());
        appConfig.setScrollbackLines((Integer) scrollbackSpinner.getValue());
        appConfig.setTerminalFont((String) fontCombo.getSelectedItem());
        appConfig.setTerminalFontSize((Integer) fontSizeSpinner.getValue());
        appConfig.setCursorBlink(cursorBlinkCheck.isSelected());
        appConfig.setAudibleBell(audibleBellCheck.isSelected());
        
        // 传输设置
        appConfig.setMaxConcurrentTransfers((Integer) maxConcurrentSpinner.getValue());
        appConfig.setTransferBufferSize((Integer) bufferSizeSpinner.getValue());
        appConfig.setConfirmOverwrite(confirmOverwriteCheck.isSelected());
        appConfig.setPreserveTimestamp(preserveTimestampCheck.isSelected());
        
        // 安全设置
        appConfig.setRememberPassword(rememberPasswordCheck.isSelected());
        appConfig.setAutoLock(autoLockCheck.isSelected());
        appConfig.setAutoLockTime((Integer) autoLockTimeSpinner.getValue());
        
        // 保存配置
        configManager.saveConfig();
        
        JOptionPane.showMessageDialog(this, "设置已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * 显示设置对话框
     */
    public static void show(Window owner, ConfigManager configManager) {
        SettingsDialog dialog = new SettingsDialog(owner, configManager);
        dialog.setVisible(true);
    }
}
