package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * Shell监控面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ShellMonPanel extends JPanel {
    
    private JTabbedPane tabbedPane;
    private InfoPanel infoPanel;
    private SpeedPanel cpuPanel;
    private SpeedPanel memPanel;
    private SpeedPanel netPanel;
    private JPanel diskPanel;
    
    public ShellMonPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        infoPanel = new InfoPanel();
        tabbedPane.addTab("系统信息", infoPanel);
        
        cpuPanel = new SpeedPanel("CPU使用率", "%", 100);
        tabbedPane.addTab("CPU", cpuPanel);
        
        memPanel = new SpeedPanel("内存使用率", "%", 100);
        tabbedPane.addTab("内存", memPanel);
        
        netPanel = new SpeedPanel("网络速度", "KB/s", 1024);
        tabbedPane.addTab("网络", netPanel);
        
        diskPanel = createDiskPanel();
        tabbedPane.addTab("磁盘", diskPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createDiskPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        String[] columns = {"文件系统", "容量", "已用", "可用", "使用率", "挂载点"};
        Object[][] data = {};
        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }
    
    public void updateCpu(double value) {
        cpuPanel.addValue(value);
    }
    
    public void updateMemory(double value) {
        memPanel.addValue(value);
    }
    
    public void updateNetwork(double rxSpeed, double txSpeed) {
        netPanel.addValue(rxSpeed + txSpeed);
    }
    
    public void updateInfo(String hostname, String os, String kernel, String uptime) {
        infoPanel.setHostname(hostname);
        infoPanel.setOs(os);
        infoPanel.setKernel(kernel);
        infoPanel.setUptime(uptime);
    }
    
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }
    
    public SpeedPanel getCpuPanel() {
        return cpuPanel;
    }
    
    public SpeedPanel getMemPanel() {
        return memPanel;
    }
    
    public SpeedPanel getNetPanel() {
        return netPanel;
    }
}
