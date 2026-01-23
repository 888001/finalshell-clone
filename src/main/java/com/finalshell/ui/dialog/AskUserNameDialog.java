package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 用户名输入对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - AskUserNameDialog
 */
public class AskUserNameDialog extends JDialog {
    
    private JTextField usernameField;
    private JButton okButton;
    private JButton cancelButton;
    private String username;
    private boolean confirmed = false;
    
    public AskUserNameDialog(Frame owner) {
        super(owner, "输入用户名", true);
        initComponents();
    }
    
    public AskUserNameDialog(Frame owner, String title) {
        super(owner, title, true);
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        
        JLabel label = new JLabel("用户名:");
        usernameField = new JTextField(20);
        usernameField.addActionListener(e -> doOk());
        
        inputPanel.add(label, BorderLayout.WEST);
        inputPanel.add(usernameField, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        okButton = new JButton("确定");
        okButton.addActionListener(e -> doOk());
        
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> doCancel());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // ESC关闭
        getRootPane().registerKeyboardAction(
            e -> doCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        pack();
        setLocationRelativeTo(getOwner());
        setResizable(false);
    }
    
    private void doOk() {
        username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名", "提示", 
                JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        confirmed = true;
        dispose();
    }
    
    private void doCancel() {
        confirmed = false;
        dispose();
    }
    
    public void setUsername(String username) {
        this.usernameField.setText(username);
    }
    
    public String getUsername() {
        return username;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public static String showDialog(Frame owner, String title, String defaultValue) {
        AskUserNameDialog dialog = new AskUserNameDialog(owner, title);
        if (defaultValue != null) {
            dialog.setUsername(defaultValue);
        }
        dialog.setVisible(true);
        return dialog.isConfirmed() ? dialog.getUsername() : null;
    }
}
