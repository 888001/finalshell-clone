package com.finalshell.ui;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.config.ProxyConfig;
import com.finalshell.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Connection Dialog - Create/Edit SSH connection
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Parameters_Reference.md - Dialog Parameters
 */
public class ConnectionDialog extends JDialog {
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionDialog.class);
    
    private final ConnectConfig config;
    private final boolean isEdit;
    private boolean confirmed = false;
    
    // Form fields
    private JTextField nameField;
    private JTextField hostField;
    private JSpinner portSpinner;
    private JTextField userField;
    private JPasswordField passwordField;
    private JCheckBox savePasswordCheck;
    private JTextArea privateKeyArea;
    private JPasswordField passphraseField;
    private JComboBox<String> charsetCombo;
    private JSpinner timeoutSpinner;
    private JTextArea memoArea;
    private JLabel proxyLabel;
    private JButton proxyBtn;
    private ProxyConfig proxyConfig;

    private String originalPassword;
    
    public ConnectionDialog(Frame owner, ConnectConfig config) {
        super(owner, config == null ? "新建连接" : "编辑连接", true);
        this.config = config != null ? config : new ConnectConfig();
        this.isEdit = config != null;
        
        initComponents();
        initLayout();
        initListeners();
        loadConfig();
        
        setSize(500, 600);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        nameField = new JTextField(20);
        hostField = new JTextField(20);
        portSpinner = new JSpinner(new SpinnerNumberModel(22, 1, 65535, 1));
        userField = new JTextField(20);
        passwordField = new JPasswordField(20);
        savePasswordCheck = new JCheckBox("保存密码", true);
        privateKeyArea = new JTextArea(3, 20);
        privateKeyArea.setLineWrap(true);
        passphraseField = new JPasswordField(20);
        charsetCombo = new JComboBox<>(new String[]{"UTF-8", "GBK", "GB2312", "ISO-8859-1"});
        timeoutSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 300, 5));
        memoArea = new JTextArea(3, 20);
        memoArea.setLineWrap(true);
        proxyLabel = new JLabel("无");
        proxyBtn = new JButton("设置代理...");
        proxyBtn.addActionListener(e -> configureProxy());
    }
    
    private void initLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel with GridBagLayout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Name
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // Host
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("主机:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(hostField, gbc);
        
        // Port
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("端口:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(portSpinner, gbc);
        
        // Username
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("用户名:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(userField, gbc);
        
        // Password
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel pwdPanel = new JPanel(new BorderLayout(5, 0));
        pwdPanel.add(passwordField, BorderLayout.CENTER);
        pwdPanel.add(savePasswordCheck, BorderLayout.EAST);
        formPanel.add(pwdPanel, gbc);
        
        // Private Key
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("私钥:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        JPanel keyPanel = new JPanel(new BorderLayout(5, 0));
        keyPanel.add(new JScrollPane(privateKeyArea), BorderLayout.CENTER);
        JButton browseBtn = new JButton("浏览...");
        browseBtn.addActionListener(e -> browsePrivateKey());
        keyPanel.add(browseBtn, BorderLayout.EAST);
        formPanel.add(keyPanel, gbc);
        
        // Passphrase
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("私钥密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(passphraseField, gbc);
        
        // Charset
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("编码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(charsetCombo, gbc);
        
        // Timeout
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        formPanel.add(new JLabel("超时(秒):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        formPanel.add(timeoutSpinner, gbc);
        
        // Proxy
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("代理:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPanel proxyPanel = new JPanel(new BorderLayout(5, 0));
        proxyPanel.add(proxyLabel, BorderLayout.CENTER);
        proxyPanel.add(proxyBtn, BorderLayout.EAST);
        formPanel.add(proxyPanel, gbc);
        
        // Memo
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("备注:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        formPanel.add(new JScrollPane(memoArea), gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton okButton = new JButton("确定");
        okButton.addActionListener(e -> onOK());
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> onCancel());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void initListeners() {
        // Close on ESC
        getRootPane().registerKeyboardAction(
            e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Enter to confirm
        getRootPane().setDefaultButton((JButton) ((JPanel) getContentPane().getComponent(1)).getComponent(0));
    }
    
    private void loadConfig() {
        if (isEdit) {
            nameField.setText(config.getName());
            hostField.setText(config.getHost());
            portSpinner.setValue(config.getPort());
            userField.setText(config.getUserName());
            originalPassword = config.getPassword();
            if (originalPassword != null && !originalPassword.isEmpty()) {
                savePasswordCheck.setSelected(true);
                if (EncryptUtil.isDESEncrypted(originalPassword)) {
                    passwordField.setText("");
                } else {
                    passwordField.setText(originalPassword);
                }
            } else {
                savePasswordCheck.setSelected(false);
                passwordField.setText("");
            }
            privateKeyArea.setText(config.getPrivateKey());
            passphraseField.setText(config.getPassphrase());
            charsetCombo.setSelectedItem(config.getCharset());
            timeoutSpinner.setValue(config.getTimeout() / 1000);
            memoArea.setText(config.getMemo());
            proxyConfig = config.getProxyConfig();
            updateProxyLabel();
        }
    }
    
    private void configureProxy() {
        ProxyDialog dialog = new ProxyDialog(this, proxyConfig);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            proxyConfig = dialog.getResult();
            updateProxyLabel();
        }
    }
    
    private void updateProxyLabel() {
        if (proxyConfig == null || !proxyConfig.isEnabled()) {
            proxyLabel.setText("无");
        } else {
            proxyLabel.setText(proxyConfig.getDisplayString());
        }
    }
    
    private void browsePrivateKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择私钥文件");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            privateKeyArea.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }
    
    private void onOK() {
        // Validate
        String name = nameField.getText().trim();
        String host = hostField.getText().trim();
        String user = userField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入名称", "验证错误", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return;
        }
        
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机地址", "验证错误", JOptionPane.WARNING_MESSAGE);
            hostField.requestFocus();
            return;
        }
        
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "验证错误", JOptionPane.WARNING_MESSAGE);
            userField.requestFocus();
            return;
        }
        
        // Save config
        config.setName(name);
        config.setHost(host);
        config.setPort((Integer) portSpinner.getValue());
        config.setUserName(user);
        
        if (savePasswordCheck.isSelected()) {
            String newPwd = new String(passwordField.getPassword());
            if (newPwd == null || newPwd.isEmpty()) {
                if (isEdit) {
                    config.setPassword(originalPassword != null ? originalPassword : "");
                } else {
                    config.setPassword("");
                }
            } else {
                config.setPassword(newPwd);
            }
        } else {
            config.setPassword("");
        }
        
        config.setPrivateKey(privateKeyArea.getText().trim());
        config.setPassphrase(new String(passphraseField.getPassword()));
        config.setCharset((String) charsetCombo.getSelectedItem());
        config.setTimeout((Integer) timeoutSpinner.getValue() * 1000);
        config.setMemo(memoArea.getText().trim());
        config.setProxyConfig(proxyConfig);
        
        ConfigManager.getInstance().saveConnection(config);
        
        confirmed = true;
        dispose();
        
        logger.info("Connection saved: {}", config.getName());
    }
    
    private void onCancel() {
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
