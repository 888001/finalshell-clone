package com.finalshell.sftp;

import com.finalshell.config.ConnectConfig;
import com.finalshell.transfer.TransTask;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * SFTP界面组件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - FtpUI
 */
public class FtpUI extends JPanel implements FtpEventListener {
    
    private FtpClient ftpClient;
    private FtpFileTree fileTree;
    private JTextField pathField;
    private JButton refreshButton;
    private JButton homeButton;
    private JButton parentButton;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    private List<TransTask> transferQueue = new ArrayList<>();
    
    public FtpUI() {
        setLayout(new BorderLayout());
        initUI();
    }
    
    private void initUI() {
        // 顶部工具栏
        JPanel toolBar = new JPanel(new BorderLayout(5, 0));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        
        homeButton = new JButton("⌂");
        homeButton.setToolTipText("主目录");
        homeButton.addActionListener(e -> goHome());
        buttonPanel.add(homeButton);
        
        parentButton = new JButton("↑");
        parentButton.setToolTipText("上级目录");
        parentButton.addActionListener(e -> goParent());
        buttonPanel.add(parentButton);
        
        refreshButton = new JButton("↻");
        refreshButton.setToolTipText("刷新");
        refreshButton.addActionListener(e -> refresh());
        buttonPanel.add(refreshButton);
        
        toolBar.add(buttonPanel, BorderLayout.WEST);
        
        pathField = new JTextField();
        pathField.addActionListener(e -> navigateTo(pathField.getText()));
        toolBar.add(pathField, BorderLayout.CENTER);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 文件树
        fileTree = new FtpFileTree();
        JScrollPane scrollPane = new JScrollPane(fileTree);
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部状态栏
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        
        statusLabel = new JLabel("未连接");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(150, 15));
        progressBar.setVisible(false);
        statusBar.add(progressBar, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
    }
    
    public void connect(ConnectConfig config) {
        ftpClient = new FtpClient(config);
        ftpClient.addListener(this);
        fileTree.setFtpClient(ftpClient);
        
        new Thread(() -> {
            try {
                ftpClient.connect();
                SwingUtilities.invokeLater(() -> {
                    onConnected();
                    fileTree.loadDirectory("/");
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    onError(e);
                });
            }
        }).start();
    }
    
    public void disconnect() {
        if (ftpClient != null) {
            ftpClient.disconnect();
            ftpClient = null;
        }
    }
    
    private void goHome() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                String home = ftpClient.pwd();
                fileTree.loadDirectory(home);
                pathField.setText(home);
            } catch (Exception e) {
                onError(e);
            }
        }
    }
    
    private void goParent() {
        String current = fileTree.getCurrentPath();
        if (current != null && !"/".equals(current)) {
            int lastSlash = current.lastIndexOf('/');
            String parent = lastSlash > 0 ? current.substring(0, lastSlash) : "/";
            fileTree.loadDirectory(parent);
            pathField.setText(parent);
        }
    }
    
    private void refresh() {
        fileTree.refresh();
    }
    
    private void navigateTo(String path) {
        if (path != null && !path.isEmpty()) {
            fileTree.loadDirectory(path);
        }
    }
    
    @Override
    public void onConnected() {
        statusLabel.setText("已连接");
    }
    
    @Override
    public void onDisconnected() {
        statusLabel.setText("已断开");
    }
    
    @Override
    public void onDirectoryChanged(String path) {
        pathField.setText(path);
    }
    
    @Override
    public void onFileListUpdated() {
        // 文件列表已更新
    }
    
    @Override
    public void onError(Exception e) {
        statusLabel.setText("错误: " + e.getMessage());
        JOptionPane.showMessageDialog(this, 
            "SFTP错误: " + e.getMessage(), 
            "错误", JOptionPane.ERROR_MESSAGE);
    }
    
    public FtpClient getFtpClient() {
        return ftpClient;
    }
    
    public FtpFileTree getFileTree() {
        return fileTree;
    }
}
