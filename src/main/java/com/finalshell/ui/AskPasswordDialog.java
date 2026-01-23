package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;
import com.finalshell.util.DesUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 密码输入对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class AskPasswordDialog extends JDialog {
    
    public static final int CMD_INPUT_PASSWORD = 51;
    public static final int CMD_LOGIN_DENIED = 53;
    public static final int CMD_CHANGE_PASSWORD = 60;
    
    private final ConnectConfig connectConfig;
    private JPasswordField passwordField;
    private JCheckBox rememberCheckbox;
    private String password;
    private boolean confirmed = false;
    
    public AskPasswordDialog(Window owner, ConnectConfig config) {
        super(owner, "输入密码", ModalityType.APPLICATION_MODAL);
        this.connectConfig = config;
        
        initComponents();
        
        setSize(350, 180);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 提示信息
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        String host = connectConfig != null ? connectConfig.getHost() : "服务器";
        JLabel infoLabel = new JLabel("请输入 " + host + " 的密码:");
        mainPanel.add(infoLabel, gbc);
        
        // 密码标签
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("密码:"), gbc);
        
        // 密码输入框
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> confirm());
        mainPanel.add(passwordField, gbc);
        
        // 记住密码
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 0;
        rememberCheckbox = new JCheckBox("记住密码", true);
        mainPanel.add(rememberCheckbox, gbc);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okBtn = new JButton("确定");
        okBtn.addActionListener(e -> confirm());
        buttonPanel.add(okBtn);
        
        JButton cancelBtn = new JButton("取消");
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        buttonPanel.add(cancelBtn);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // ESC关闭
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void confirm() {
        password = new String(passwordField.getPassword());
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 如果选择记住密码，加密保存
        if (rememberCheckbox.isSelected() && connectConfig != null) {
            String encrypted = DesUtil.encrypt(password);
            connectConfig.setEncryptedPassword(encrypted);
        }
        
        confirmed = true;
        dispose();
    }
    
    public String getPassword() {
        return password;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public boolean isRememberPassword() {
        return rememberCheckbox.isSelected();
    }
    
    /**
     * 静态方法显示对话框
     */
    public static String showDialog(Window owner, ConnectConfig config) {
        AskPasswordDialog dialog = new AskPasswordDialog(owner, config);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            return dialog.getPassword();
        }
        return null;
    }
}
