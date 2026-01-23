package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * RDP连接面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class RdpConnectPanel extends JPanel {
    
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField domainField;
    private JComboBox<String> resolutionCombo;
    private JCheckBox fullScreenCheck;
    private JCheckBox shareClipboardCheck;
    private JCheckBox shareDrivesCheck;
    private JCheckBox sharePrintersCheck;
    
    public RdpConnectPanel() {
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
        gbc.gridx = 1; gbc.weightx = 1.0;
        hostField = new JTextField(20);
        add(hostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        portField = new JTextField("3389");
        add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = new JTextField();
        add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        add(passwordField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("域:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        domainField = new JTextField();
        add(domainField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        add(new JLabel("分辨率:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        resolutionCombo = new JComboBox<>(new String[]{
            "自动", "1920x1080", "1680x1050", "1440x900", "1366x768", "1280x1024", "1024x768"
        });
        add(resolutionCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        fullScreenCheck = new JCheckBox("全屏模式");
        add(fullScreenCheck, gbc);
        
        row++;
        gbc.gridy = row;
        shareClipboardCheck = new JCheckBox("共享剪贴板");
        shareClipboardCheck.setSelected(true);
        add(shareClipboardCheck, gbc);
        
        row++;
        gbc.gridy = row;
        shareDrivesCheck = new JCheckBox("共享本地驱动器");
        add(shareDrivesCheck, gbc);
        
        row++;
        gbc.gridy = row;
        sharePrintersCheck = new JCheckBox("共享打印机");
        add(sharePrintersCheck, gbc);
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
            return 3389;
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
    
    public String getDomain() {
        return domainField.getText().trim();
    }
    
    public void setDomain(String domain) {
        domainField.setText(domain);
    }
    
    public String getResolution() {
        return (String) resolutionCombo.getSelectedItem();
    }
    
    public boolean isFullScreen() {
        return fullScreenCheck.isSelected();
    }
    
    public boolean isShareClipboard() {
        return shareClipboardCheck.isSelected();
    }
    
    public boolean isShareDrives() {
        return shareDrivesCheck.isSelected();
    }
    
    public boolean isSharePrinters() {
        return sharePrintersCheck.isSelected();
    }
}
