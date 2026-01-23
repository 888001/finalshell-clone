package com.finalshell.ui;

import com.finalshell.config.ProxyConfig;

import javax.swing.*;
import java.awt.*;

/**
 * Proxy Configuration Dialog
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyDialog extends JDialog {
    
    private JComboBox<ProxyConfig.ProxyType> typeCombo;
    
    // HTTP/SOCKS fields
    private JTextField proxyHostField;
    private JSpinner proxyPortSpinner;
    private JTextField proxyUserField;
    private JPasswordField proxyPassField;
    
    // Jump host fields
    private JTextField jumpHostField;
    private JSpinner jumpPortSpinner;
    private JTextField jumpUserField;
    private JPasswordField jumpPassField;
    private JTextField jumpKeyField;
    private JButton jumpKeyBrowseBtn;
    
    private JPanel proxyPanel;
    private JPanel jumpPanel;
    
    private ProxyConfig result;
    private boolean confirmed = false;
    
    public ProxyDialog(Window owner) {
        this(owner, null);
    }
    
    public ProxyDialog(Window owner, ProxyConfig existing) {
        super(owner, "代理设置", ModalityType.APPLICATION_MODAL);
        
        initComponents();
        initLayout();
        
        if (existing != null) {
            loadConfig(existing);
        }
        
        setSize(450, 350);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        typeCombo = new JComboBox<>(ProxyConfig.ProxyType.values());
        typeCombo.addActionListener(e -> updateVisibility());
        
        // Proxy fields
        proxyHostField = new JTextField(20);
        proxyPortSpinner = new JSpinner(new SpinnerNumberModel(1080, 1, 65535, 1));
        proxyUserField = new JTextField(15);
        proxyPassField = new JPasswordField(15);
        
        // Jump host fields
        jumpHostField = new JTextField(20);
        jumpPortSpinner = new JSpinner(new SpinnerNumberModel(22, 1, 65535, 1));
        jumpUserField = new JTextField(15);
        jumpPassField = new JPasswordField(15);
        jumpKeyField = new JTextField(20);
        jumpKeyBrowseBtn = new JButton("...");
        jumpKeyBrowseBtn.addActionListener(e -> browseKey());
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Type selection
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.add(new JLabel("代理类型:"));
        typePanel.add(typeCombo);
        mainPanel.add(typePanel, BorderLayout.NORTH);
        
        // Card panel for different proxy types
        JPanel cardPanel = new JPanel(new CardLayout());
        
        // Empty panel for NONE
        cardPanel.add(new JPanel(), "NONE");
        
        // HTTP/SOCKS panel
        proxyPanel = createProxyPanel();
        cardPanel.add(proxyPanel, "PROXY");
        
        // Jump host panel
        jumpPanel = createJumpPanel();
        cardPanel.add(jumpPanel, "JUMP");
        
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> confirm());
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        getRootPane().setDefaultButton(okBtn);
        updateVisibility();
    }
    
    private JPanel createProxyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("代理服务器"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Host
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(proxyHostField, gbc);
        
        // Port
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1;
        panel.add(proxyPortSpinner, gbc);
        
        // Username
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(proxyUserField, gbc);
        
        // Password
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(proxyPassField, gbc);
        
        return panel;
    }
    
    private JPanel createJumpPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("跳板机"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Host
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(jumpHostField, gbc);
        
        // Port
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1;
        panel.add(jumpPortSpinner, gbc);
        
        // Username
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(jumpUserField, gbc);
        
        // Password
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(jumpPassField, gbc);
        
        // Private key
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("私钥:"), gbc);
        gbc.gridx = 1;
        JPanel keyPanel = new JPanel(new BorderLayout(5, 0));
        keyPanel.add(jumpKeyField, BorderLayout.CENTER);
        keyPanel.add(jumpKeyBrowseBtn, BorderLayout.EAST);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        panel.add(keyPanel, gbc);
        
        return panel;
    }
    
    private void updateVisibility() {
        ProxyConfig.ProxyType type = (ProxyConfig.ProxyType) typeCombo.getSelectedItem();
        Container parent = proxyPanel.getParent();
        if (parent != null && parent.getLayout() instanceof CardLayout) {
            CardLayout cl = (CardLayout) parent.getLayout();
            switch (type) {
                case NONE:
                    cl.show(parent, "NONE");
                    break;
                case JUMP_HOST:
                    cl.show(parent, "JUMP");
                    break;
                default:
                    cl.show(parent, "PROXY");
                    break;
            }
        }
    }
    
    private void browseKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择私钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            jumpKeyField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void loadConfig(ProxyConfig config) {
        typeCombo.setSelectedItem(config.getType());
        
        proxyHostField.setText(config.getProxyHost());
        proxyPortSpinner.setValue(config.getProxyPort());
        proxyUserField.setText(config.getProxyUsername());
        proxyPassField.setText(config.getProxyPassword());
        
        jumpHostField.setText(config.getJumpHost());
        jumpPortSpinner.setValue(config.getJumpPort());
        jumpUserField.setText(config.getJumpUsername());
        jumpPassField.setText(config.getJumpPassword());
        jumpKeyField.setText(config.getJumpPrivateKey());
        
        updateVisibility();
    }
    
    private void confirm() {
        ProxyConfig.ProxyType type = (ProxyConfig.ProxyType) typeCombo.getSelectedItem();
        
        if (type != ProxyConfig.ProxyType.NONE) {
            if (type == ProxyConfig.ProxyType.JUMP_HOST) {
                if (jumpHostField.getText().trim().isEmpty()) {
                    showError("请输入跳板机主机地址");
                    return;
                }
                if (jumpUserField.getText().trim().isEmpty()) {
                    showError("请输入跳板机用户名");
                    return;
                }
            } else {
                if (proxyHostField.getText().trim().isEmpty()) {
                    showError("请输入代理服务器地址");
                    return;
                }
            }
        }
        
        result = new ProxyConfig();
        result.setType(type);
        
        result.setProxyHost(proxyHostField.getText().trim());
        result.setProxyPort((Integer) proxyPortSpinner.getValue());
        result.setProxyUsername(proxyUserField.getText().trim());
        result.setProxyPassword(new String(proxyPassField.getPassword()));
        
        result.setJumpHost(jumpHostField.getText().trim());
        result.setJumpPort((Integer) jumpPortSpinner.getValue());
        result.setJumpUsername(jumpUserField.getText().trim());
        result.setJumpPassword(new String(jumpPassField.getPassword()));
        result.setJumpPrivateKey(jumpKeyField.getText().trim());
        
        confirmed = true;
        dispose();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "输入错误", JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public ProxyConfig getResult() {
        return result;
    }
}
