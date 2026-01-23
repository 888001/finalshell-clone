package com.finalshell.network;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 网络管理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetManagerPanel extends JPanel {
    
    private JTabbedPane tabbedPane;
    private NetPanel netPanel;
    private JPanel socketPanel;
    private JButton refreshAllButton;
    
    public NetManagerPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        
        netPanel = new NetPanel();
        socketPanel = createSocketPanel();
        
        tabbedPane.addTab("TCP/UDP连接", netPanel);
        tabbedPane.addTab("Unix Socket", socketPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshAllButton = new JButton("全部刷新");
        refreshAllButton.addActionListener(e -> refreshAll());
        buttonPanel.add(refreshAllButton);
        
        add(tabbedPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSocketPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    
    public void refreshAll() {
        netPanel.refresh();
    }
    
    public NetPanel getNetPanel() {
        return netPanel;
    }
}
