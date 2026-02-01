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
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        mainPanel.add(infoLabel, gbc);
        
        // 密码输入框标签
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = new JLabel("输入密码:");
        mainPanel.add(passwordLabel, gbc);
        
        // 密码输入框
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmPassword();
                }
            }
        });
        mainPanel.add(passwordField, gbc);
        
        // 记住密码复选框
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        rememberCheckbox = new JCheckBox("记住密码", false);
        rememberCheckbox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        mainPanel.add(rememberCheckbox, gbc);
        
        // 按钮面板
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton okButton = new JButton("确定");
        okButton.setPreferredSize(new Dimension(80, 30));
        okButton.addActionListener(e -> confirmPassword());
        
        JButton cancelButton = new JButton("取消");
        cancelButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.addActionListener(e -> {
            confirmed = false;
            setVisible(false);
        });
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    public AskPasswordDialog(Window owner, ConnectConfig config, int command) {
        this(owner, config);
        
        // 根据命令显示不同的提示信息
        String message = "";
        switch (command) {
            case CMD_INPUT_PASSWORD:
                message = "输入密码";
                break;
            case CMD_LOGIN_DENIED:
                message = "禁止登录!";
                break;
            case CMD_CHANGE_PASSWORD:
                message = "需要修改密码";
                break;
            default:
                message = "输入密码";
                break;
        }
        
        if (command != CMD_INPUT_PASSWORD) {
            JLabel msgLabel = new JLabel(message);
            msgLabel.setForeground(new Color(200, 50, 50));
            msgLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            add(msgLabel, BorderLayout.NORTH);
        }
    }
    
    private void confirmPassword() {
        char[] passwordChars = passwordField.getPassword();
        password = new String(passwordChars);
        
        if (password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "密码不能为空!", 
                "错误", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.requestFocus();
            return;
        }
        
        // 如果选择记住密码，加密存储
        if (rememberCheckbox.isSelected() && connectConfig != null) {
            try {
                String encryptedPassword = DesUtil.encrypt(password);
                connectConfig.setPassword(encryptedPassword);
                connectConfig.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        confirmed = true;
        setVisible(false);
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
