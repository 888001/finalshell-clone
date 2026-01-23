package com.finalshell.ui;

import com.finalshell.sync.SyncConfig;
import com.finalshell.sync.SyncManager;

import javax.swing.*;
import java.awt.*;

/**
 * 同步配置面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md
 */
public class SyncConfigPanel extends JPanel {
    
    private JCheckBox enableSyncCheckbox;
    private JTextField emailField;
    private JButton changePwdButton;
    private JButton syncNowButton;
    private JLabel statusLabel;
    
    private SyncManager syncManager;
    
    public SyncConfigPanel() {
        this.syncManager = SyncManager.getInstance();
        initUI();
        loadConfig();
    }
    
    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("云同步设置"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        enableSyncCheckbox = new JCheckBox("启用云同步");
        enableSyncCheckbox.addActionListener(e -> updateUI());
        add(enableSyncCheckbox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("同步账号:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setEditable(false);
        add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        changePwdButton = new JButton("修改密码");
        syncNowButton = new JButton("立即同步");
        
        changePwdButton.addActionListener(e -> changePassword());
        syncNowButton.addActionListener(e -> syncNow());
        
        buttonPanel.add(changePwdButton);
        buttonPanel.add(syncNowButton);
        add(buttonPanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        statusLabel = new JLabel("状态: 未同步");
        statusLabel.setForeground(Color.GRAY);
        add(statusLabel, gbc);
    }
    
    private void loadConfig() {
        SyncConfig config = syncManager.getConfig();
        if (config != null) {
            enableSyncCheckbox.setSelected(config.isEnabled());
            emailField.setText(config.getEmail());
            updateUI();
        }
    }
    
    private void updateUI() {
        boolean enabled = enableSyncCheckbox.isSelected();
        changePwdButton.setEnabled(enabled);
        syncNowButton.setEnabled(enabled);
    }
    
    private void changePassword() {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        com.finalshell.ui.dialog.SycPwdSettingDialog dialog = 
            new com.finalshell.ui.dialog.SycPwdSettingDialog(owner);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            statusLabel.setText("状态: 密码已修改");
            statusLabel.setForeground(new Color(50, 150, 50));
        }
    }
    
    private void syncNow() {
        statusLabel.setText("状态: 同步中...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return syncManager.sync();
            }
            
            @Override
            protected void done() {
                try {
                    if (get()) {
                        statusLabel.setText("状态: 同步成功");
                        statusLabel.setForeground(new Color(50, 150, 50));
                    } else {
                        statusLabel.setText("状态: 同步失败");
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    statusLabel.setText("状态: 同步出错");
                    statusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }
    
    public void saveConfig() {
        SyncConfig config = syncManager.getConfig();
        if (config == null) {
            config = new SyncConfig();
        }
        config.setEnabled(enableSyncCheckbox.isSelected());
        syncManager.setConfig(config);
    }
}
