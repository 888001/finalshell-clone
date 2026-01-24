package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * 系统信息面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class InfoPanel extends JPanel {
    
    private JLabel hostnameLabel;
    private JLabel osLabel;
    private JLabel kernelLabel;
    private JLabel uptimeLabel;
    private JLabel cpuLabel;
    private JLabel memoryLabel;
    private JLabel loadLabel;
    
    public InfoPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridLayout(7, 2, 10, 5));
        setBorder(BorderFactory.createTitledBorder("系统信息"));
        
        add(new JLabel("主机名:"));
        hostnameLabel = new JLabel("-");
        add(hostnameLabel);
        
        add(new JLabel("操作系统:"));
        osLabel = new JLabel("-");
        add(osLabel);
        
        add(new JLabel("内核版本:"));
        kernelLabel = new JLabel("-");
        add(kernelLabel);
        
        add(new JLabel("运行时间:"));
        uptimeLabel = new JLabel("-");
        add(uptimeLabel);
        
        add(new JLabel("CPU:"));
        cpuLabel = new JLabel("-");
        add(cpuLabel);
        
        add(new JLabel("内存:"));
        memoryLabel = new JLabel("-");
        add(memoryLabel);
        
        add(new JLabel("负载:"));
        loadLabel = new JLabel("-");
        add(loadLabel);
    }
    
    public void updateInfo(MonitorData data) {
        if (data == null) {
            return;
        }
        
        hostnameLabel.setText(data.getHostname() != null ? data.getHostname() : "-");
        osLabel.setText(data.getOsName() != null ? data.getOsName() : "-");
        kernelLabel.setText(data.getKernelVersion() != null ? data.getKernelVersion() : "-");
        uptimeLabel.setText(MonitorData.formatUptime(data.getUptime()));
        
        if (data.getCpuInfo() != null) {
            cpuLabel.setText(data.getCpuInfo().toString());
        }
        
        memoryLabel.setText(String.format("%s / %s", 
            formatSize(data.getMemUsed()), 
            formatSize(data.getMemTotal())));
        
        loadLabel.setText(String.format("%.2f, %.2f, %.2f", 
            data.getLoad1(), data.getLoad5(), data.getLoad15()));
    }
    
    public void setHostname(String hostname) {
        hostnameLabel.setText(hostname != null ? hostname : "-");
    }
    
    public void setOs(String os) {
        osLabel.setText(os != null ? os : "-");
    }
    
    public void setKernel(String kernel) {
        kernelLabel.setText(kernel != null ? kernel : "-");
    }
    
    public void setUptime(String uptime) {
        uptimeLabel.setText(uptime != null ? uptime : "-");
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    public void clear() {
        hostnameLabel.setText("-");
        osLabel.setText("-");
        kernelLabel.setText("-");
        uptimeLabel.setText("-");
        cpuLabel.setText("-");
        memoryLabel.setText("-");
        loadLabel.setText("-");
    }
}
