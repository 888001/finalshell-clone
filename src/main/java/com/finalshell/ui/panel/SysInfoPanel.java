package com.finalshell.ui.panel;

import com.finalshell.monitor.MonitorData;
import com.finalshell.monitor.MonitorScanner;
import com.finalshell.ui.BaseTabPanel;
import com.finalshell.ui.TabWrap;

import javax.swing.*;
import java.awt.*;

/**
 * 系统信息监控面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md
 */
public class SysInfoPanel extends BaseTabPanel {
    
    private TabWrap tabWrap;
    private MonitorScanner scanner;
    private MonitorData monitorData;
    private boolean running = false;
    
    private JLabel cpuLabel;
    private JLabel memLabel;
    private JLabel diskLabel;
    private JLabel networkLabel;
    private JProgressBar cpuBar;
    private JProgressBar memBar;
    
    private MainInfoPanel mainInfoPanel;
    
    public SysInfoPanel() {
        initUI();
    }
    
    public SysInfoPanel(TabWrap tabWrap) {
        this.tabWrap = tabWrap;
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // 顶部信息栏
        JPanel topPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(new JLabel("CPU:"));
        cpuLabel = new JLabel("0%");
        topPanel.add(cpuLabel);
        
        topPanel.add(new JLabel("内存:"));
        memLabel = new JLabel("0%");
        topPanel.add(memLabel);
        
        topPanel.add(new JLabel("磁盘:"));
        diskLabel = new JLabel("-");
        topPanel.add(diskLabel);
        
        topPanel.add(new JLabel("网络:"));
        networkLabel = new JLabel("-");
        topPanel.add(networkLabel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 进度条面板
        JPanel progressPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        progressPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        cpuBar = new JProgressBar(0, 100);
        cpuBar.setStringPainted(true);
        cpuBar.setString("CPU: 0%");
        progressPanel.add(cpuBar);
        
        memBar = new JProgressBar(0, 100);
        memBar.setStringPainted(true);
        memBar.setString("内存: 0%");
        progressPanel.add(memBar);
        
        add(progressPanel, BorderLayout.CENTER);
        
        // 主信息面板
        mainInfoPanel = new MainInfoPanel();
        add(mainInfoPanel, BorderLayout.SOUTH);
    }
    
    public void setMonitorData(MonitorData data) {
        this.monitorData = data;
        updateUI();
    }
    
    private void updateUI() {
        if (monitorData == null) return;
        
        SwingUtilities.invokeLater(() -> {
            double cpu = monitorData.getCpuUsage();
            double mem = monitorData.getMemUsage();
            
            cpuLabel.setText(String.format("%.1f%%", cpu));
            memLabel.setText(String.format("%.1f%%", mem));
            
            cpuBar.setValue((int) cpu);
            cpuBar.setString(String.format("CPU: %.1f%%", cpu));
            
            memBar.setValue((int) mem);
            memBar.setString(String.format("内存: %.1f%%", mem));
        });
    }
    
    public void startMonitor() {
        running = true;
    }
    
    public void stopMonitor() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public TabWrap getTabWrap() {
        return tabWrap;
    }
    
    public void setTabWrap(TabWrap tabWrap) {
        this.tabWrap = tabWrap;
    }
}
