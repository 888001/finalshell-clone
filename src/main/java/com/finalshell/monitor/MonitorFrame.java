package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 监控窗口
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MonitorFrame extends JFrame {
    
    private MonitorPanel monitorPanel;
    private SpeedPanel speedPanel;
    private JButton refreshButton;
    private JButton closeButton;
    
    public MonitorFrame() {
        super("系统监控");
        initUI();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        monitorPanel = new MonitorPanel();
        speedPanel = new SpeedPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
            monitorPanel, speedPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.7);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("刷新");
        closeButton = new JButton("关闭");
        
        refreshButton.addActionListener(e -> refresh());
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    public void refresh() {
        if (monitorPanel != null) {
            monitorPanel.refresh();
        }
    }
    
    public void updateMonitorData(MonitorData data) {
        if (monitorPanel != null) {
            monitorPanel.updateData(data);
        }
        
        if (speedPanel != null && data.getNetInfoList() != null && !data.getNetInfoList().isEmpty()) {
            long totalRx = 0;
            long totalTx = 0;
            for (NetInfo info : data.getNetInfoList()) {
                totalRx += info.getRxBytes();
                totalTx += info.getTxBytes();
            }
            speedPanel.updateSpeed(totalRx, totalTx);
        }
    }
    
    public MonitorPanel getMonitorPanel() {
        return monitorPanel;
    }
    
    public SpeedPanel getSpeedPanel() {
        return speedPanel;
    }
}
