package com.finalshell.proxy;

import javax.swing.*;
import java.awt.*;

/**
 * 添加代理对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AddProxyDialog extends JDialog {
    
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JTextField hostField;
    private JTextField portField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox defaultCheck;
    private JButton okButton;
    private JButton cancelButton;
    
    private boolean confirmed;
    private ProxyInfo proxyInfo;
    
    public AddProxyDialog(Window owner) {
        this(owner, null);
    }
    
    public AddProxyDialog(Window owner, ProxyInfo proxy) {
        super(owner, proxy == null ? "添加代理" : "编辑代理", ModalityType.APPLICATION_MODAL);
        this.proxyInfo = proxy != null ? proxy : new ProxyInfo();
        initUI();
        loadData();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(350, 300);
        setLocationRelativeTo(getOwner());
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("类型:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        typeCombo = new JComboBox<>(new String[]{"无", "HTTP", "SOCKS4", "SOCKS5"});
        formPanel.add(typeCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        hostField = new JTextField();
        formPanel.add(hostField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        portField = new JTextField("1080");
        formPanel.add(portField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = new JTextField();
        formPanel.add(usernameField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField();
        formPanel.add(passwordField, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        defaultCheck = new JCheckBox("设为默认代理");
        formPanel.add(defaultCheck, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("确定");
        cancelButton = new JButton("取消");
        
        okButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadData() {
        nameField.setText(proxyInfo.getName());
        typeCombo.setSelectedIndex(proxyInfo.getType());
        hostField.setText(proxyInfo.getHost());
        portField.setText(String.valueOf(proxyInfo.getPort()));
        usernameField.setText(proxyInfo.getUsername());
        passwordField.setText(proxyInfo.getPassword());
        defaultCheck.setSelected(proxyInfo.isDefaultProxy());
    }
    
    private void confirm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称");
            return;
        }
        
        try {
            proxyInfo.setName(nameField.getText().trim());
            proxyInfo.setType(typeCombo.getSelectedIndex());
            proxyInfo.setHost(hostField.getText().trim());
            proxyInfo.setPort(Integer.parseInt(portField.getText().trim()));
            proxyInfo.setUsername(usernameField.getText());
            proxyInfo.setPassword(new String(passwordField.getPassword()));
            proxyInfo.setDefaultProxy(defaultCheck.isSelected());
            
            confirmed = true;
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "端口必须是数字");
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public ProxyInfo getProxyInfo() {
        return proxyInfo;
    }
}
