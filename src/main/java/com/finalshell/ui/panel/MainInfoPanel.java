package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * 主信息面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md
 */
public class MainInfoPanel extends JPanel {
    
    private JLabel hostnameLabel;
    private JLabel osLabel;
    private JLabel kernelLabel;
    private JLabel uptimeLabel;
    private JLabel loadLabel;
    
    public MainInfoPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridLayout(5, 2, 10, 5));
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
        
        add(new JLabel("系统负载:"));
        loadLabel = new JLabel("-");
        add(loadLabel);
    }
    
    public void setHostname(String hostname) {
        hostnameLabel.setText(hostname != null ? hostname : "-");
    }
    
    public void setOS(String os) {
        osLabel.setText(os != null ? os : "-");
    }
    
    public void setKernel(String kernel) {
        kernelLabel.setText(kernel != null ? kernel : "-");
    }
    
    public void setUptime(String uptime) {
        uptimeLabel.setText(uptime != null ? uptime : "-");
    }
    
    public void setLoad(String load) {
        loadLabel.setText(load != null ? load : "-");
    }
    
    public void setInfo(String hostname, String os, String kernel, String uptime, String load) {
        setHostname(hostname);
        setOS(os);
        setKernel(kernel);
        setUptime(uptime);
        setLoad(load);
    }
}
