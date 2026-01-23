package com.finalshell.control;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 登录对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Control_Auth_DeepAnalysis.md
 */
public class LoginDialog extends JDialog {
    
    public static String message = "";
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    private JCheckBox rememberCheckBox;
    
    private boolean loggedIn = false;
    private LoginCallback callback;
    
    public LoginDialog(Window owner) {
        super(owner, "登录", ModalityType.APPLICATION_MODAL);
        initComponents();
        setSize(350, 220);
        setLocationRelativeTo(owner);
        setResizable(false);
    }
    
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // 表单面板
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // 用户名
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("用户名:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // 密码
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("密码:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // 记住密码
        gbc.gridx = 1; gbc.gridy = 2;
        rememberCheckBox = new JCheckBox("记住密码");
        formPanel.add(rememberCheckBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // 消息标签
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, BorderLayout.NORTH);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        loginButton = new JButton("登录");
        cancelButton = new JButton("取消");
        
        loginButton.addActionListener(e -> doLogin());
        cancelButton.addActionListener(e -> {
            loggedIn = false;
            dispose();
        });
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // 回车登录
        getRootPane().setDefaultButton(loginButton);
    }
    
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty()) {
            messageLabel.setText("请输入用户名");
            return;
        }
        if (password.isEmpty()) {
            messageLabel.setText("请输入密码");
            return;
        }
        
        loginButton.setEnabled(false);
        messageLabel.setText("登录中...");
        messageLabel.setForeground(Color.BLUE);
        
        ControlClient.getInstance().login(username, password, (success, code, isPro) -> {
            SwingUtilities.invokeLater(() -> {
                loginButton.setEnabled(true);
                if (success) {
                    loggedIn = true;
                    if (callback != null) {
                        callback.onLogin(username, isPro);
                    }
                    dispose();
                } else {
                    messageLabel.setText(message.isEmpty() ? "登录失败" : message);
                    messageLabel.setForeground(Color.RED);
                }
            });
        });
    }
    
    public void setCallback(LoginCallback callback) {
        this.callback = callback;
    }
    
    public boolean isLoggedIn() { return loggedIn; }
    
    /**
     * 登录回调
     */
    public interface LoginCallback {
        void onLogin(String username, boolean isPro);
    }
}
