package com.finalshell.network;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Traceroute面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertPanel extends JPanel {
    
    private TracertTable table;
    private TracertTableModel tableModel;
    private JButton startButton;
    private JButton stopButton;
    private JTextField hostField;
    private JLabel statusLabel;
    private boolean running;
    private Thread tracertThread;
    
    public TracertPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hostField = new JTextField(20);
        startButton = new JButton("开始");
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        
        startButton.addActionListener(e -> start(hostField.getText()));
        stopButton.addActionListener(e -> stop());
        
        topPanel.add(new JLabel("目标主机:"));
        topPanel.add(hostField);
        topPanel.add(startButton);
        topPanel.add(stopButton);
        
        tableModel = new TracertTableModel();
        table = new TracertTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        
        statusLabel = new JLabel("就绪");
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void start(String host) {
        if (running || host == null || host.trim().isEmpty()) {
            return;
        }
        
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        tableModel.clear();
        statusLabel.setText("正在追踪路由到 " + host + "...");
        
        tracertThread = new Thread(() -> {
            try {
                executeTracert(host.trim());
            } finally {
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    statusLabel.setText("完成");
                });
            }
        });
        tracertThread.start();
    }
    
    public void stop() {
        running = false;
        if (tracertThread != null) {
            tracertThread.interrupt();
        }
        statusLabel.setText("已停止");
    }
    
    private void executeTracert(String host) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String[] command;
            if (os.contains("win")) {
                command = new String[]{"tracert", "-d", host};
            } else {
                command = new String[]{"traceroute", "-n", host};
            }
            
            Process process = Runtime.getRuntime().exec(command);
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            
            String line;
            int hop = 0;
            while ((line = reader.readLine()) != null && running) {
                TracertNode node = parseLine(line, ++hop);
                if (node != null) {
                    SwingUtilities.invokeLater(() -> tableModel.addNode(node));
                }
            }
            
            reader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private TracertNode parseLine(String line, int hop) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        TracertNode node = new TracertNode();
        node.setHop(hop);
        node.setRawLine(line);
        
        String[] parts = line.trim().split("\\s+");
        for (String part : parts) {
            if (part.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                node.setIpAddress(part);
                break;
            }
        }
        
        return node;
    }
    
    public TracertTable getTable() {
        return table;
    }
    
    public boolean isRunning() {
        return running;
    }
}
