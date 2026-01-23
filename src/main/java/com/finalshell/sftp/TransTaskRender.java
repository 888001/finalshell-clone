package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * 传输任务渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TransTaskRender implements TableCellRenderer {
    
    private JPanel panel;
    private JLabel iconLabel;
    private JLabel nameLabel;
    private JLabel sizeLabel;
    private TransProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel speedLabel;
    
    public TransTaskRender() {
        initUI();
    }
    
    private void initUI() {
        panel = new JPanel(new BorderLayout(5, 2));
        panel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        iconLabel = new JLabel();
        nameLabel = new JLabel();
        sizeLabel = new JLabel();
        sizeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(nameLabel, BorderLayout.CENTER);
        topPanel.add(sizeLabel, BorderLayout.EAST);
        
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        progressBar = new TransProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 16));
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        statusLabel = new JLabel();
        speedLabel = new JLabel();
        statusPanel.add(statusLabel);
        statusPanel.add(speedLabel);
        
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(statusPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(bottomPanel, BorderLayout.CENTER);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        if (value instanceof TransferTask) {
            TransferTask task = (TransferTask) value;
            
            nameLabel.setText(task.getFileName());
            sizeLabel.setText(formatSize(task.getTotalSize()));
            progressBar.setValue(task.getProgress());
            progressBar.setString(task.getProgress() + "%");
            
            switch (task.getStatus()) {
                case RUNNING:
                    statusLabel.setText("传输中");
                    speedLabel.setText(formatSpeed(task.getSpeed()));
                    progressBar.setPaused(false);
                    progressBar.setError(false);
                    break;
                case PAUSED:
                    statusLabel.setText("已暂停");
                    speedLabel.setText("");
                    progressBar.setPaused(true);
                    progressBar.setError(false);
                    break;
                case COMPLETED:
                    statusLabel.setText("已完成");
                    speedLabel.setText("");
                    progressBar.setValue(100);
                    progressBar.setPaused(false);
                    progressBar.setError(false);
                    break;
                case FAILED:
                    statusLabel.setText("失败");
                    speedLabel.setText("");
                    progressBar.setPaused(false);
                    progressBar.setError(true);
                    break;
                case WAITING:
                    statusLabel.setText("等待中");
                    speedLabel.setText("");
                    progressBar.setPaused(false);
                    progressBar.setError(false);
                    break;
            }
        }
        
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        return panel;
    }
    
    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
    
    private String formatSpeed(long speed) {
        if (speed < 1024) return speed + " B/s";
        if (speed < 1024 * 1024) return String.format("%.1f KB/s", speed / 1024.0);
        return String.format("%.1f MB/s", speed / (1024.0 * 1024));
    }
}
