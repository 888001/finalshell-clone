package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import java.awt.*;

/**
 * 导航面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Nav_Loading_UI_DeepAnalysis.md - NavPanel
 */
public class NavPanel extends JPanel {
    
    private ConnListPanel connListPanel;
    private JLabel titleLabel;
    
    public NavPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));
        setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220, 150)));
        
        // 标题
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        titleLabel = new JLabel("连接列表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);
        
        // 连接列表
        connListPanel = new ConnListPanel();
        add(connListPanel, BorderLayout.CENTER);
        
        // 底部工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        toolBar.setOpaque(false);
        
        JButton newConnBtn = new JButton("新建连接");
        JButton importBtn = new JButton("导入");
        
        toolBar.add(newConnBtn);
        toolBar.add(importBtn);
        add(toolBar, BorderLayout.SOUTH);
    }
    
    public ConnListPanel getConnListPanel() { return connListPanel; }
    
    public void setConnListListener(ConnListPanel.ConnListListener listener) {
        connListPanel.setListener(listener);
    }
    
    /**
     * 打开连接
     */
    public void openConnection(ConnectConfig config, MainWindow mainWindow) {
        if (config != null && mainWindow != null) {
            mainWindow.openConnection(config);
        }
    }
}
