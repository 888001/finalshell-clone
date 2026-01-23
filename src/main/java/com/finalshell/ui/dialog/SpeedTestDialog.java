package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 速度测试对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class SpeedTestDialog extends JDialog {
    
    private JTextField hostField;
    private JButton startButton;
    private JButton stopButton;
    private JProgressBar downloadProgress;
    private JProgressBar uploadProgress;
    private JLabel downloadLabel;
    private JLabel uploadLabel;
    private JLabel statusLabel;
    private volatile boolean running = false;
    
    public SpeedTestDialog(Frame owner) {
        super(owner, "网络速度测试", true);
        initUI();
    }
    
    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 主机输入
        JPanel hostPanel = new JPanel(new BorderLayout(5, 0));
        hostPanel.add(new JLabel("测试服务器:"), BorderLayout.WEST);
        hostField = new JTextField("speedtest.tele2.net");
        hostPanel.add(hostField, BorderLayout.CENTER);
        hostPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(hostPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 下载速度
        JPanel downloadPanel = new JPanel(new BorderLayout(5, 0));
        downloadPanel.add(new JLabel("下载速度:"), BorderLayout.WEST);
        downloadLabel = new JLabel("-- Mbps");
        downloadPanel.add(downloadLabel, BorderLayout.EAST);
        downloadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mainPanel.add(downloadPanel);
        
        downloadProgress = new JProgressBar(0, 100);
        downloadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        mainPanel.add(downloadProgress);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 上传速度
        JPanel uploadPanel = new JPanel(new BorderLayout(5, 0));
        uploadPanel.add(new JLabel("上传速度:"), BorderLayout.WEST);
        uploadLabel = new JLabel("-- Mbps");
        uploadPanel.add(uploadLabel, BorderLayout.EAST);
        uploadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mainPanel.add(uploadPanel);
        
        uploadProgress = new JProgressBar(0, 100);
        uploadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        mainPanel.add(uploadProgress);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 状态
        statusLabel = new JLabel("就绪");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        startButton = new JButton("开始测试");
        startButton.addActionListener(e -> startTest());
        buttonPanel.add(startButton);
        
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopTest());
        buttonPanel.add(stopButton);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void startTest() {
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        downloadProgress.setValue(0);
        uploadProgress.setValue(0);
        downloadLabel.setText("测试中...");
        uploadLabel.setText("等待...");
        statusLabel.setText("正在进行下载测试...");
        
        new Thread(() -> {
            try {
                // 模拟下载测试
                for (int i = 0; i <= 100 && running; i += 5) {
                    final int progress = i;
                    SwingUtilities.invokeLater(() -> downloadProgress.setValue(progress));
                    Thread.sleep(100);
                }
                
                if (running) {
                    double downloadSpeed = Math.random() * 100 + 10;
                    SwingUtilities.invokeLater(() -> {
                        downloadLabel.setText(String.format("%.2f Mbps", downloadSpeed));
                        statusLabel.setText("正在进行上传测试...");
                    });
                    
                    // 模拟上传测试
                    for (int i = 0; i <= 100 && running; i += 5) {
                        final int progress = i;
                        SwingUtilities.invokeLater(() -> uploadProgress.setValue(progress));
                        Thread.sleep(100);
                    }
                    
                    if (running) {
                        double uploadSpeed = Math.random() * 50 + 5;
                        SwingUtilities.invokeLater(() -> {
                            uploadLabel.setText(String.format("%.2f Mbps", uploadSpeed));
                            statusLabel.setText("测试完成");
                        });
                    }
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> statusLabel.setText("错误: " + e.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        }).start();
    }
    
    private void stopTest() {
        running = false;
        statusLabel.setText("已停止");
    }
}
