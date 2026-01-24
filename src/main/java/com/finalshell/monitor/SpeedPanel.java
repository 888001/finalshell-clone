package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 网络速度面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SpeedPanel extends JPanel {
    
    private SpeedCanvas canvas;
    private JLabel rxLabel;
    private JLabel txLabel;
    private JLabel totalRxLabel;
    private JLabel totalTxLabel;
    
    private long lastRxBytes = 0;
    private long lastTxBytes = 0;
    private long totalRxBytes = 0;
    private long totalTxBytes = 0;
    private long lastUpdateTime = 0;
    
    public SpeedPanel() {
        initUI();
    }
    
    public SpeedPanel(String title, String unit, int maxValue) {
        initUI();
        setBorder(BorderFactory.createTitledBorder(title));
    }
    
    public void addValue(double value) {
        canvas.addData(value, 0);
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("网络速度"));
        
        canvas = new SpeedCanvas();
        add(canvas, BorderLayout.CENTER);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 5, 2));
        rxLabel = new JLabel("下载: 0 KB/s");
        txLabel = new JLabel("上传: 0 KB/s");
        totalRxLabel = new JLabel("总下载: 0 MB");
        totalTxLabel = new JLabel("总上传: 0 MB");
        
        rxLabel.setForeground(new Color(50, 150, 50));
        txLabel.setForeground(new Color(50, 50, 200));
        
        infoPanel.add(rxLabel);
        infoPanel.add(txLabel);
        infoPanel.add(totalRxLabel);
        infoPanel.add(totalTxLabel);
        
        add(infoPanel, BorderLayout.SOUTH);
    }
    
    public void updateSpeed(long rxBytes, long txBytes) {
        long currentTime = System.currentTimeMillis();
        
        if (lastUpdateTime > 0) {
            long timeDiff = currentTime - lastUpdateTime;
            if (timeDiff > 0) {
                long rxDiff = rxBytes - lastRxBytes;
                long txDiff = txBytes - lastTxBytes;
                
                if (rxDiff >= 0 && txDiff >= 0) {
                    double rxSpeed = (rxDiff * 1000.0) / timeDiff;
                    double txSpeed = (txDiff * 1000.0) / timeDiff;
                    
                    totalRxBytes += rxDiff;
                    totalTxBytes += txDiff;
                    
                    canvas.addData(rxSpeed, txSpeed);
                    
                    rxLabel.setText("下载: " + formatSpeed(rxSpeed));
                    txLabel.setText("上传: " + formatSpeed(txSpeed));
                    totalRxLabel.setText("总下载: " + formatSize(totalRxBytes));
                    totalTxLabel.setText("总上传: " + formatSize(totalTxBytes));
                }
            }
        }
        
        lastRxBytes = rxBytes;
        lastTxBytes = txBytes;
        lastUpdateTime = currentTime;
    }
    
    private String formatSpeed(double bytesPerSec) {
        if (bytesPerSec < 1024) {
            return String.format("%.0f B/s", bytesPerSec);
        } else if (bytesPerSec < 1024 * 1024) {
            return String.format("%.1f KB/s", bytesPerSec / 1024);
        } else {
            return String.format("%.2f MB/s", bytesPerSec / (1024 * 1024));
        }
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024L * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    public void reset() {
        lastRxBytes = 0;
        lastTxBytes = 0;
        totalRxBytes = 0;
        totalTxBytes = 0;
        lastUpdateTime = 0;
        canvas.clear();
    }
}
