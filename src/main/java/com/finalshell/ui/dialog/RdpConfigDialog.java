package com.finalshell.ui.dialog;

import com.finalshell.rdp.RDPConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * RDP远程桌面连接配置对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class RdpConfigDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField domainField;
    private JComboBox<String> resolutionCombo;
    private JCheckBox fullScreenCheckbox;
    private JCheckBox rememberPasswordCheckbox;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private RDPConfig config;
    private boolean confirmed = false;
    
    public RdpConfigDialog(Frame owner) {
        super(owner, "远程桌面配置", true);
        initUI();
        setSize(400, 350);
        setLocationRelativeTo(owner);
    }
    
    public RdpConfigDialog(Frame owner, RDPConfig config) {
        this(owner);
        this.config = config;
        loadConfig();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        nameField = new JTextField(20);
        mainPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hostField = new JTextField(20);
        mainPanel.add(hostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        portField = new JTextField("3389", 6);
        mainPanel.add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("域:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        domainField = new JTextField(20);
        mainPanel.add(domainField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("分辨率:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        resolutionCombo = new JComboBox<>(new String[]{
            "自动", "1920x1080", "1680x1050", "1440x900", "1366x768", "1280x1024", "1024x768"
        });
        mainPanel.add(resolutionCombo, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        fullScreenCheckbox = new JCheckBox("全屏模式");
        mainPanel.add(fullScreenCheckbox, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        rememberPasswordCheckbox = new JCheckBox("记住密码", true);
        mainPanel.add(rememberPasswordCheckbox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton("确定");
        cancelButton = new JButton("取消");
        
        confirmButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadConfig() {
        if (config != null) {
            nameField.setText(config.getName());
            hostField.setText(config.getHost());
            portField.setText(String.valueOf(config.getPort()));
            usernameField.setText(config.getUsername());
            passwordField.setText(config.getPassword());
            domainField.setText(config.getDomain());
            fullScreenCheckbox.setSelected(config.isFullScreen());
            rememberPasswordCheckbox.setSelected(config.isRememberPassword());
            
            String resolution = config.getResolution();
            if (resolution != null) {
                resolutionCombo.setSelectedItem(resolution);
            }
        }
    }
    
    private void confirm() {
        if (hostField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机地址", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (config == null) {
            config = new RDPConfig();
        }
        
        config.setName(nameField.getText().trim());
        config.setHost(hostField.getText().trim());
        try {
            config.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException e) {
            config.setPort(3389);
        }
        config.setUsername(usernameField.getText().trim());
        config.setPassword(new String(passwordField.getPassword()));
        config.setDomain(domainField.getText().trim());
        config.setResolution((String) resolutionCombo.getSelectedItem());
        config.setFullScreen(fullScreenCheckbox.isSelected());
        config.setRememberPassword(rememberPasswordCheckbox.isSelected());
        
        confirmed = true;
        dispose();
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public RDPConfig getConfig() {
        return config;
    }
}
