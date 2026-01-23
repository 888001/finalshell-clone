package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 同步密码设置对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class SycPwdSettingDialog extends JDialog {
    
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton confirmButton;
    private JButton cancelButton;
    
    private boolean confirmed = false;
    
    public SycPwdSettingDialog(Frame owner) {
        super(owner, "设置同步密码", true);
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
        mainPanel.add(new JLabel("当前密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        currentPasswordField = new JPasswordField(15);
        mainPanel.add(currentPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("新密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        newPasswordField = new JPasswordField(15);
        mainPanel.add(newPasswordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("确认密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        confirmPasswordField = new JPasswordField(15);
        mainPanel.add(confirmPasswordField, gbc);
        
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
    
    private void confirm() {
        String newPwd = new String(newPasswordField.getPassword());
        String confirmPwd = new String(confirmPasswordField.getPassword());
        
        if (newPwd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入新密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!newPwd.equals(confirmPwd)) {
            JOptionPane.showMessageDialog(this, "两次密码输入不一致", "提示", JOptionPane.WARNING_MESSAGE);
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
    
    public String getCurrentPassword() {
        return new String(currentPasswordField.getPassword());
    }
    
    public String getNewPassword() {
        return new String(newPasswordField.getPassword());
    }
}
