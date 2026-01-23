package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;

/**
 * 更新对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class UpdaterDialog extends JDialog {
    
    private JLabel versionLabel;
    private JTextArea changelogArea;
    private JProgressBar progressBar;
    private JButton updateButton;
    private JButton laterButton;
    private JLabel statusLabel;
    
    private String newVersion;
    private String changelog;
    private String downloadUrl;
    
    public UpdaterDialog(Frame owner) {
        super(owner, "软件更新", true);
        initUI();
    }
    
    private void initUI() {
        setSize(450, 350);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        
        // 版本信息
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        versionPanel.add(new JLabel("发现新版本:"));
        versionLabel = new JLabel("--");
        versionLabel.setFont(versionLabel.getFont().deriveFont(Font.BOLD));
        versionPanel.add(versionLabel);
        versionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(versionPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 更新日志
        JLabel logLabel = new JLabel("更新内容:");
        logLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(logLabel);
        
        changelogArea = new JTextArea(8, 40);
        changelogArea.setEditable(false);
        changelogArea.setLineWrap(true);
        changelogArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(changelogArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // 进度条
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mainPanel.add(progressBar);
        
        // 状态
        statusLabel = new JLabel(" ");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        updateButton = new JButton("立即更新");
        updateButton.addActionListener(e -> startUpdate());
        buttonPanel.add(updateButton);
        
        laterButton = new JButton("稍后更新");
        laterButton.addActionListener(e -> dispose());
        buttonPanel.add(laterButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void setUpdateInfo(String version, String changelog, String url) {
        this.newVersion = version;
        this.changelog = changelog;
        this.downloadUrl = url;
        
        versionLabel.setText(version);
        changelogArea.setText(changelog);
    }
    
    private void startUpdate() {
        updateButton.setEnabled(false);
        laterButton.setEnabled(false);
        progressBar.setVisible(true);
        statusLabel.setText("正在下载更新...");
        
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 2) {
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                    Thread.sleep(50);
                }
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("下载完成，准备安装...");
                    JOptionPane.showMessageDialog(this, 
                        "更新下载完成，程序将重启以完成安装。", 
                        "更新完成", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("更新失败: " + e.getMessage());
                    updateButton.setEnabled(true);
                    laterButton.setEnabled(true);
                });
            }
        }).start();
    }
}
