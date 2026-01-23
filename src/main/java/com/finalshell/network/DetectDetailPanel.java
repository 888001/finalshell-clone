package com.finalshell.network;

import javax.swing.*;
import java.awt.*;

/**
 * 网络检测详情面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class DetectDetailPanel extends JPanel {
    
    private JTextArea detailArea;
    private JLabel ipLabel;
    private JLabel hostnameLabel;
    private JLabel locationLabel;
    private JLabel ispLabel;
    
    public DetectDetailPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("详细信息"));
        
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        
        infoPanel.add(new JLabel("IP地址:"));
        ipLabel = new JLabel("-");
        infoPanel.add(ipLabel);
        
        infoPanel.add(new JLabel("主机名:"));
        hostnameLabel = new JLabel("-");
        infoPanel.add(hostnameLabel);
        
        infoPanel.add(new JLabel("地理位置:"));
        locationLabel = new JLabel("-");
        infoPanel.add(locationLabel);
        
        infoPanel.add(new JLabel("ISP:"));
        ispLabel = new JLabel("-");
        infoPanel.add(ispLabel);
        
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(detailArea);
        
        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void showDetail(String ip, String hostname, String location, String isp) {
        ipLabel.setText(ip != null ? ip : "-");
        hostnameLabel.setText(hostname != null ? hostname : "-");
        locationLabel.setText(location != null ? location : "-");
        ispLabel.setText(isp != null ? isp : "-");
    }
    
    public void showDetail(TracertNode node) {
        if (node == null) {
            clear();
            return;
        }
        
        ipLabel.setText(node.getIpAddress() != null ? node.getIpAddress() : "-");
        hostnameLabel.setText(node.getHostname() != null ? node.getHostname() : "-");
        locationLabel.setText(node.getLocation() != null ? node.getLocation() : "-");
        ispLabel.setText("-");
        
        detailArea.setText(node.getRawLine());
    }
    
    public void appendDetail(String text) {
        detailArea.append(text + "\n");
        detailArea.setCaretPosition(detailArea.getDocument().getLength());
    }
    
    public void clear() {
        ipLabel.setText("-");
        hostnameLabel.setText("-");
        locationLabel.setText("-");
        ispLabel.setText("-");
        detailArea.setText("");
    }
}
