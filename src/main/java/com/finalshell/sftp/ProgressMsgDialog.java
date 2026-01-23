package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 进度消息对话框
 * 显示操作进度和消息
 */
public class ProgressMsgDialog extends JDialog {
    
    private JLabel messageLabel;
    private JProgressBar progressBar;
    private JButton cancelButton;
    private boolean cancelled = false;
    
    public ProgressMsgDialog(Frame owner, String title) {
        super(owner, title, false);
        initUI();
    }
    
    public ProgressMsgDialog(Dialog owner, String title) {
        super(owner, title, false);
        initUI();
    }
    
    private void initUI() {
        setSize(350, 120);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        messageLabel = new JLabel("处理中...");
        panel.add(messageLabel, BorderLayout.NORTH);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        panel.add(progressBar, BorderLayout.CENTER);
        
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> {
            cancelled = true;
            setVisible(false);
        });
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(cancelButton);
        panel.add(btnPanel, BorderLayout.SOUTH);
        
        setContentPane(panel);
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    public void setProgress(int value) {
        progressBar.setValue(value);
    }
    
    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    public void reset() {
        cancelled = false;
        progressBar.setValue(0);
        messageLabel.setText("处理中...");
    }
}
