package com.finalshell.ui.dialog;

import com.finalshell.config.ProxyConfig;

import javax.swing.*;
import java.awt.*;

/**
 * 添加代理对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class AddProxyDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private ProxyConfig config;
    private boolean confirmed = false;
    
    public AddProxyDialog(Frame owner) {
        super(owner, "添加代理", true);
        initUI();
        setSize(350, 300);
        setLocationRelativeTo(owner);
    }
    
    public AddProxyDialog(Frame owner, ProxyConfig config) {
        this(owner);
        this.config = config;
        setTitle("编辑代理");
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
        nameField = new JTextField(15);
        mainPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{"SOCKS5", "SOCKS4", "HTTP"});
        mainPanel.add(typeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        hostField = new JTextField(15);
        mainPanel.add(hostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        portField = new JTextField("1080", 6);
        mainPanel.add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        mainPanel.add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        mainPanel.add(passwordField, gbc);
        
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
            typeCombo.setSelectedItem(config.getType());
            hostField.setText(config.getHost());
            portField.setText(String.valueOf(config.getPort()));
            usernameField.setText(config.getUsername());
            passwordField.setText(config.getPassword());
        }
    }
    
    private void confirm() {
        if (hostField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入代理主机", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (config == null) {
            config = new ProxyConfig();
        }
        
        config.setName(nameField.getText().trim());
        config.setType((String) typeCombo.getSelectedItem());
        config.setHost(hostField.getText().trim());
        try {
            config.setPort(Integer.parseInt(portField.getText().trim()));
        } catch (NumberFormatException e) {
            config.setPort(1080);
        }
        config.setUsername(usernameField.getText().trim());
        config.setPassword(new String(passwordField.getPassword()));
        
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
    
    public ProxyConfig getConfig() {
        return config;
    }
}
