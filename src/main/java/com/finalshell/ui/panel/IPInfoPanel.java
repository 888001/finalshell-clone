package com.finalshell.ui.panel;

import com.finalshell.network.IPInfo;

import javax.swing.*;
import java.awt.*;

/**
 * IP信息面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md
 */
public class IPInfoPanel extends JPanel {
    
    private JLabel ipLabel;
    private JLabel countryLabel;
    private JLabel regionLabel;
    private JLabel cityLabel;
    private JLabel ispLabel;
    
    public IPInfoPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridLayout(5, 2, 10, 5));
        setBorder(BorderFactory.createTitledBorder("IP信息"));
        
        add(new JLabel("IP地址:"));
        ipLabel = new JLabel("-");
        add(ipLabel);
        
        add(new JLabel("国家:"));
        countryLabel = new JLabel("-");
        add(countryLabel);
        
        add(new JLabel("地区:"));
        regionLabel = new JLabel("-");
        add(regionLabel);
        
        add(new JLabel("城市:"));
        cityLabel = new JLabel("-");
        add(cityLabel);
        
        add(new JLabel("运营商:"));
        ispLabel = new JLabel("-");
        add(ispLabel);
    }
    
    public void setIPInfo(IPInfo info) {
        if (info != null) {
            ipLabel.setText(info.getIp() != null ? info.getIp() : "-");
            countryLabel.setText(info.getCountry() != null ? info.getCountry() : "-");
            regionLabel.setText(info.getRegion() != null ? info.getRegion() : "-");
            cityLabel.setText(info.getCity() != null ? info.getCity() : "-");
            ispLabel.setText(info.getIsp() != null ? info.getIsp() : "-");
        } else {
            clearInfo();
        }
    }
    
    public void clearInfo() {
        ipLabel.setText("-");
        countryLabel.setText("-");
        regionLabel.setText("-");
        cityLabel.setText("-");
        ispLabel.setText("-");
    }
}
