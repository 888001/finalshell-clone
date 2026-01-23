package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 邮箱同步对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class EmailSyncDialog extends JDialog {
    
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton syncButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;
    
    public EmailSyncDialog(Frame owner) {
        super(owner, "邮箱同步", true);
        initUI();
        setSize(350, 200);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("邮箱地址:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        emailField = new JTextField(20);
        mainPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("同步密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        JLabel hintLabel = new JLabel("注: 同步密码用于加密云端数据");
        hintLabel.setFont(hintLabel.getFont().deriveFont(11f));
        hintLabel.setForeground(Color.GRAY);
        mainPanel.add(hintLabel, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        syncButton = new JButton("同步");
        cancelButton = new JButton("取消");
        
        syncButton.addActionListener(e -> sync());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(syncButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void sync() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入邮箱地址", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入同步密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
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
    
    public String getEmail() {
        return emailField.getText().trim();
    }
    
    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}
