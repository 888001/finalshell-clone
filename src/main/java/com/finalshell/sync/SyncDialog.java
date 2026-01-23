package com.finalshell.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Sync Dialog - Data synchronization settings
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SyncDialog extends JDialog implements SyncManager.SyncListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SyncDialog.class);
    
    private final SyncManager syncManager;
    private final SyncConfig config;
    
    private JComboBox<SyncConfig.SyncType> typeCombo;
    private JTextField localPathField;
    private JButton browseBtn;
    private JTextField webdavUrlField;
    private JTextField webdavUserField;
    private JPasswordField webdavPassField;
    
    private JCheckBox syncConnectionsCheck;
    private JCheckBox syncCommandsCheck;
    private JCheckBox syncSettingsCheck;
    private JCheckBox encryptCheck;
    
    private JButton exportBtn;
    private JButton importBtn;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    public SyncDialog(Frame owner) {
        super(owner, "数据同步", true);
        this.syncManager = SyncManager.getInstance();
        this.config = syncManager.getSyncConfig();
        
        initComponents();
        initLayout();
        initListeners();
        loadConfig();
        
        syncManager.addListener(this);
        
        setSize(500, 450);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        typeCombo = new JComboBox<>(SyncConfig.SyncType.values());
        
        localPathField = new JTextField(25);
        browseBtn = new JButton("浏览...");
        
        webdavUrlField = new JTextField(25);
        webdavUserField = new JTextField(15);
        webdavPassField = new JPasswordField(15);
        
        syncConnectionsCheck = new JCheckBox("连接配置", true);
        syncCommandsCheck = new JCheckBox("快捷命令", true);
        syncSettingsCheck = new JCheckBox("应用设置", true);
        encryptCheck = new JCheckBox("加密备份", true);
        
        exportBtn = new JButton("导出数据");
        importBtn = new JButton("导入数据");
        
        statusLabel = new JLabel("就绪");
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
    }
    
    private void initLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Settings panel
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("同步设置"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Sync type
        gbc.gridx = 0; gbc.gridy = row;
        settingsPanel.add(new JLabel("同步方式:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        settingsPanel.add(typeCombo, gbc);
        
        // Local path
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        settingsPanel.add(new JLabel("本地路径:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel pathPanel = new JPanel(new BorderLayout(5, 0));
        pathPanel.add(localPathField, BorderLayout.CENTER);
        pathPanel.add(browseBtn, BorderLayout.EAST);
        settingsPanel.add(pathPanel, gbc);
        
        // WebDAV URL
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        settingsPanel.add(new JLabel("WebDAV地址:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        settingsPanel.add(webdavUrlField, gbc);
        
        // WebDAV credentials
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        settingsPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel credPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        credPanel.add(webdavUserField);
        credPanel.add(new JLabel("密码:"));
        credPanel.add(webdavPassField);
        settingsPanel.add(credPanel, gbc);
        
        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        
        // Data selection panel
        JPanel dataPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        dataPanel.setBorder(BorderFactory.createTitledBorder("同步内容"));
        dataPanel.add(syncConnectionsCheck);
        dataPanel.add(syncCommandsCheck);
        dataPanel.add(syncSettingsCheck);
        dataPanel.add(encryptCheck);
        
        mainPanel.add(dataPanel, BorderLayout.CENTER);
        
        // Actions panel
        JPanel actionsPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(exportBtn);
        buttonPanel.add(importBtn);
        actionsPanel.add(buttonPanel, BorderLayout.CENTER);
        
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.EAST);
        actionsPanel.add(statusPanel, BorderLayout.SOUTH);
        
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void initListeners() {
        // Type change
        typeCombo.addActionListener(e -> updateFieldsVisibility());
        
        // Browse
        browseBtn.addActionListener(e -> browseLocalPath());
        
        // Export
        exportBtn.addActionListener(e -> exportData());
        
        // Import
        importBtn.addActionListener(e -> importData());
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void loadConfig() {
        typeCombo.setSelectedItem(config.getType());
        localPathField.setText(config.getLocalPath());
        webdavUrlField.setText(config.getWebdavUrl());
        webdavUserField.setText(config.getWebdavUsername());
        webdavPassField.setText(config.getWebdavPassword());
        syncConnectionsCheck.setSelected(config.isSyncConnections());
        syncCommandsCheck.setSelected(config.isSyncQuickCommands());
        syncSettingsCheck.setSelected(config.isSyncSettings());
        encryptCheck.setSelected(config.isEncryptBackup());
        
        updateFieldsVisibility();
    }
    
    private void saveConfig() {
        config.setType((SyncConfig.SyncType) typeCombo.getSelectedItem());
        config.setLocalPath(localPathField.getText().trim());
        config.setWebdavUrl(webdavUrlField.getText().trim());
        config.setWebdavUsername(webdavUserField.getText().trim());
        config.setWebdavPassword(new String(webdavPassField.getPassword()));
        config.setSyncConnections(syncConnectionsCheck.isSelected());
        config.setSyncQuickCommands(syncCommandsCheck.isSelected());
        config.setSyncSettings(syncSettingsCheck.isSelected());
        config.setEncryptBackup(encryptCheck.isSelected());
        
        syncManager.saveSyncConfig();
    }
    
    private void updateFieldsVisibility() {
        SyncConfig.SyncType type = (SyncConfig.SyncType) typeCombo.getSelectedItem();
        
        boolean isLocal = type == SyncConfig.SyncType.LOCAL;
        boolean isWebdav = type == SyncConfig.SyncType.WEBDAV;
        
        localPathField.setEnabled(isLocal);
        browseBtn.setEnabled(isLocal);
        webdavUrlField.setEnabled(isWebdav);
        webdavUserField.setEnabled(isWebdav);
        webdavPassField.setEnabled(isWebdav);
    }
    
    private void browseLocalPath() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择备份目录");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            localPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void exportData() {
        saveConfig();
        
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出数据");
        chooser.setSelectedFile(new File(syncManager.generateBackupFilename()));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().endsWith(".zip")) {
                file = new File(file.getAbsolutePath() + ".zip");
            }
            
            final File targetFile = file;
            progressBar.setVisible(true);
            exportBtn.setEnabled(false);
            importBtn.setEnabled(false);
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    syncManager.exportToFile(targetFile);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    exportBtn.setEnabled(true);
                    importBtn.setEnabled(true);
                    try {
                        get();
                        JOptionPane.showMessageDialog(SyncDialog.this,
                            "数据导出成功!", "导出完成", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SyncDialog.this,
                            "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void importData() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导入数据");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("ZIP文件", "zip"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            int result = JOptionPane.showConfirmDialog(this,
                "导入将覆盖现有数据，确定继续?",
                "确认导入", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
            
            final File sourceFile = chooser.getSelectedFile();
            progressBar.setVisible(true);
            exportBtn.setEnabled(false);
            importBtn.setEnabled(false);
            
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    syncManager.importFromFile(sourceFile);
                    return null;
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    exportBtn.setEnabled(true);
                    importBtn.setEnabled(true);
                    try {
                        get();
                        JOptionPane.showMessageDialog(SyncDialog.this,
                            "数据导入成功! 部分设置需重启应用生效。",
                            "导入完成", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SyncDialog.this,
                            "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    @Override
    public void onSyncEvent(SyncManager.SyncEvent event, String message) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(message));
    }
    
    @Override
    public void dispose() {
        syncManager.removeListener(this);
        super.dispose();
    }
}
