package com.finalshell.ui.dialog;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * SSH连接配置对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class SshConfigDialog extends JDialog {
    
    private JTextField nameField;
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> authTypeCombo;
    private JTextField keyPathField;
    private JButton keyBrowseButton;
    private JCheckBox rememberPasswordCheckbox;
    private JTextField charsetField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private ConnectConfig config;
    private boolean confirmed = false;
    
    public SshConfigDialog(Frame owner) {
        super(owner, "SSH连接配置", true);
        initUI();
        setSize(450, 400);
        setLocationRelativeTo(owner);
    }
    
    public SshConfigDialog(Frame owner, ConnectConfig config) {
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
        portField = new JTextField("22", 6);
        mainPanel.add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("认证方式:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        authTypeCombo = new JComboBox<>(new String[]{"密码", "公钥"});
        authTypeCombo.addActionListener(e -> updateAuthUI());
        mainPanel.add(authTypeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("密钥路径:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel keyPanel = new JPanel(new BorderLayout(5, 0));
        keyPathField = new JTextField(15);
        keyBrowseButton = new JButton("浏览...");
        keyBrowseButton.addActionListener(e -> browseKeyFile());
        keyPanel.add(keyPathField, BorderLayout.CENTER);
        keyPanel.add(keyBrowseButton, BorderLayout.EAST);
        mainPanel.add(keyPanel, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        rememberPasswordCheckbox = new JCheckBox("记住密码", true);
        mainPanel.add(rememberPasswordCheckbox, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("编码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        charsetField = new JTextField("UTF-8", 10);
        mainPanel.add(charsetField, gbc);
        
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
        
        updateAuthUI();
    }
    
    private void updateAuthUI() {
        boolean isPassword = authTypeCombo.getSelectedIndex() == 0;
        passwordField.setEnabled(isPassword);
        keyPathField.setEnabled(!isPassword);
        keyBrowseButton.setEnabled(!isPassword);
    }
    
    private void browseKeyFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择私钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            keyPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void loadConfig() {
        if (config != null) {
            nameField.setText(config.getName());
            hostField.setText(config.getHost());
            portField.setText(String.valueOf(config.getPort()));
            usernameField.setText(config.getUsername());
            passwordField.setText(config.getPassword());
            charsetField.setText(config.getCharset() != null ? config.getCharset() : "UTF-8");
            rememberPasswordCheckbox.setSelected(config.isRememberPassword());
            if (config.getKeyPath() != null && !config.getKeyPath().isEmpty()) {
                authTypeCombo.setSelectedIndex(1);
                keyPathField.setText(config.getKeyPath());
            }
            updateAuthUI();
        }
    }
    
    private void confirm() {
        if (hostField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机地址", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (config == null) {
            config = new ConnectConfig();
        }
        
        config.setName(nameField.getText().trim());
        config.setHost(hostField.getText().trim());
        try {
            config.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException e) {
            config.setPort(22);
        }
        config.setUsername(usernameField.getText().trim());
        config.setPassword(new String(passwordField.getPassword()));
        config.setCharset(charsetField.getText().trim());
        config.setRememberPassword(rememberPasswordCheckbox.isSelected());
        
        if (authTypeCombo.getSelectedIndex() == 1) {
            config.setKeyPath(keyPathField.getText().trim());
        } else {
            config.setKeyPath(null);
        }
        
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
    
    public ConnectConfig getConfig() {
        return config;
    }
}
