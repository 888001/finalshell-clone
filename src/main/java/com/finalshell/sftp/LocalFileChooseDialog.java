package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * 本地文件选择对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class LocalFileChooseDialog extends JDialog {
    
    private LocalFilePanel filePanel;
    private JButton selectButton;
    private JButton cancelButton;
    
    private File[] selectedFiles;
    private boolean confirmed = false;
    
    public LocalFileChooseDialog(Frame owner) {
        super(owner, "选择文件", true);
        initUI();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    public LocalFileChooseDialog(Frame owner, String title) {
        super(owner, title, true);
        initUI();
        setSize(500, 400);
        setLocationRelativeTo(owner);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        filePanel = new LocalFilePanel();
        filePanel.goHome();
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        selectButton = new JButton("选择");
        cancelButton = new JButton("取消");
        
        selectButton.addActionListener(e -> confirm());
        cancelButton.addActionListener(e -> cancel());
        
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(filePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void confirm() {
        selectedFiles = filePanel.getSelectedFiles();
        if (selectedFiles != null && selectedFiles.length > 0) {
            confirmed = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "请选择文件", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void cancel() {
        confirmed = false;
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public File[] getSelectedFiles() {
        return selectedFiles;
    }
    
    public File getSelectedFile() {
        if (selectedFiles != null && selectedFiles.length > 0) {
            return selectedFiles[0];
        }
        return null;
    }
    
    public static File[] showDialog(Frame owner) {
        LocalFileChooseDialog dialog = new LocalFileChooseDialog(owner);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            return dialog.getSelectedFiles();
        }
        return null;
    }
    
    public static File showSingleFileDialog(Frame owner) {
        LocalFileChooseDialog dialog = new LocalFileChooseDialog(owner);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            return dialog.getSelectedFile();
        }
        return null;
    }
}
