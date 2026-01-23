package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * SSH连接面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SshConnectPanel extends JPanel {
    
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> authMethodCombo;
    private JTextField keyFileField;
    private JButton browseKeyButton;
    private JPasswordField passphraseField;
    private JComboBox<String> charsetCombo;
    private JCheckBox compressionCheck;
    private JCheckBox keepAliveCheck;
    
    public SshConnectPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        hostField = new JTextField(20);
        add(hostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        portField = new JTextField("22");
        add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        usernameField = new JTextField();
        add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("认证方式:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        authMethodCombo = new JComboBox<>(new String[]{"密码", "公钥", "密码+公钥"});
        add(authMethodCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        passwordField = new JPasswordField();
        add(passwordField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("密钥文件:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 1;
        keyFileField = new JTextField();
        add(keyFileField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        browseKeyButton = new JButton("...");
        add(browseKeyButton, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("密钥密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        passphraseField = new JPasswordField();
        add(passphraseField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        add(new JLabel("字符集:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        charsetCombo = new JComboBox<>(new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1"});
        add(charsetCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
        compressionCheck = new JCheckBox("启用压缩");
        add(compressionCheck, gbc);
        
        row++;
        gbc.gridy = row;
        keepAliveCheck = new JCheckBox("保持连接");
        keepAliveCheck.setSelected(true);
        add(keepAliveCheck, gbc);
    }
    
    public String getHost() {
        return hostField.getText().trim();
    }
    
    public void setHost(String host) {
        hostField.setText(host);
    }
    
    public int getPort() {
        try {
            return Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            return 22;
        }
    }
    
    public void setPort(int port) {
        portField.setText(String.valueOf(port));
    }
    
    public String getUsername() {
        return usernameField.getText().trim();
    }
    
    public void setUsername(String username) {
        usernameField.setText(username);
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
    
    public void setPassword(String password) {
        passwordField.setText(password);
    }
    
    public String getAuthMethod() {
        return (String) authMethodCombo.getSelectedItem();
    }
    
    public String getKeyFile() {
        return keyFileField.getText().trim();
    }
    
    public void setKeyFile(String keyFile) {
        keyFileField.setText(keyFile);
    }
    
    public String getPassphrase() {
        return new String(passphraseField.getPassword());
    }
    
    public String getCharset() {
        return (String) charsetCombo.getSelectedItem();
    }
    
    public boolean isCompression() {
        return compressionCheck.isSelected();
    }
    
    public boolean isKeepAlive() {
        return keepAliveCheck.isSelected();
    }
}
