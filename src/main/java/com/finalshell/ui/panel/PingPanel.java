package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ping工具面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class PingPanel extends JPanel {
    
    private JTextField hostField;
    private JButton pingButton;
    private JButton stopButton;
    private JTextArea resultArea;
    private PingCanvas pingCanvas;
    private volatile boolean running = false;
    
    public PingPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // 顶部控制栏
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        
        hostField = new JTextField();
        hostField.setToolTipText("输入IP地址或域名");
        topPanel.add(hostField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pingButton = new JButton("Ping");
        pingButton.addActionListener(e -> startPing());
        buttonPanel.add(pingButton);
        
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopPing());
        buttonPanel.add(stopButton);
        
        topPanel.add(buttonPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        // Ping图形显示
        pingCanvas = new PingCanvas();
        pingCanvas.setPreferredSize(new Dimension(0, 100));
        add(pingCanvas, BorderLayout.CENTER);
        
        // 结果文本区
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        add(scrollPane, BorderLayout.SOUTH);
        
        hostField.addActionListener(e -> startPing());
    }
    
    private void startPing() {
        String host = hostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入IP地址或域名", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        running = true;
        pingButton.setEnabled(false);
        stopButton.setEnabled(true);
        resultArea.setText("");
        pingCanvas.clear();
        
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("ping", "-n", "10", host);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream(), "GBK"));
                
                String line;
                while (running && (line = reader.readLine()) != null) {
                    final String output = line;
                    SwingUtilities.invokeLater(() -> {
                        resultArea.append(output + "\n");
                        resultArea.setCaretPosition(resultArea.getDocument().getLength());
                        
                        // 解析延迟并更新图形
                        int delay = parseDelay(output);
                        if (delay >= 0) {
                            pingCanvas.addValue(delay);
                        }
                    });
                }
                
                reader.close();
                process.waitFor();
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultArea.append("错误: " + e.getMessage() + "\n");
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    pingButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        }).start();
    }
    
    private void stopPing() {
        running = false;
    }
    
    private int parseDelay(String line) {
        // 解析 "时间=XXXms" 格式
        int idx = line.indexOf("时间=");
        if (idx < 0) idx = line.indexOf("time=");
        if (idx >= 0) {
            try {
                int start = idx + (line.contains("时间=") ? 3 : 5);
                int end = line.indexOf("ms", start);
                if (end > start) {
                    String delayStr = line.substring(start, end).trim();
                    return Integer.parseInt(delayStr);
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        return -1;
    }
}
