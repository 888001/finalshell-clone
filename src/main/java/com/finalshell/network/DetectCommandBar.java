package com.finalshell.network;

import javax.swing.*;
import java.awt.*;

/**
 * 网络检测命令栏
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class DetectCommandBar extends JPanel {
    
    private JTextField hostField;
    private JButton pingButton;
    private JButton tracertButton;
    private JButton whoisButton;
    private JButton stopButton;
    private JComboBox<String> historyCombo;
    
    public DetectCommandBar() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        add(new JLabel("主机:"));
        hostField = new JTextField(20);
        add(hostField);
        
        pingButton = new JButton("Ping");
        tracertButton = new JButton("Traceroute");
        whoisButton = new JButton("Whois");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        
        add(pingButton);
        add(tracertButton);
        add(whoisButton);
        add(stopButton);
        
        historyCombo = new JComboBox<>();
        historyCombo.setPreferredSize(new Dimension(150, 25));
        historyCombo.addActionListener(e -> {
            String selected = (String) historyCombo.getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                hostField.setText(selected);
            }
        });
        add(new JLabel("历史:"));
        add(historyCombo);
    }
    
    public String getHost() {
        return hostField.getText().trim();
    }
    
    public void setHost(String host) {
        hostField.setText(host);
    }
    
    public JButton getPingButton() {
        return pingButton;
    }
    
    public JButton getTracertButton() {
        return tracertButton;
    }
    
    public JButton getWhoisButton() {
        return whoisButton;
    }
    
    public JButton getStopButton() {
        return stopButton;
    }
    
    public void addToHistory(String host) {
        if (host == null || host.isEmpty()) {
            return;
        }
        for (int i = 0; i < historyCombo.getItemCount(); i++) {
            if (host.equals(historyCombo.getItemAt(i))) {
                return;
            }
        }
        historyCombo.insertItemAt(host, 0);
        if (historyCombo.getItemCount() > 20) {
            historyCombo.removeItemAt(historyCombo.getItemCount() - 1);
        }
    }
    
    public void setRunning(boolean running) {
        pingButton.setEnabled(!running);
        tracertButton.setEnabled(!running);
        whoisButton.setEnabled(!running);
        stopButton.setEnabled(running);
        hostField.setEnabled(!running);
    }
}
