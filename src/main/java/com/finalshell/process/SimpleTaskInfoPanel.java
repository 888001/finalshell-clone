package com.finalshell.process;

import javax.swing.*;
import java.awt.*;

/**
 * 简单任务信息面板
 * 显示进程的基本信息
 */
public class SimpleTaskInfoPanel extends JPanel {
    
    private JLabel pidLabel;
    private JLabel nameLabel;
    private JLabel cpuLabel;
    private JLabel memLabel;
    private JLabel statusLabel;
    private JLabel userLabel;
    private JLabel commandLabel;
    
    private TaskRow currentTask;
    
    public SimpleTaskInfoPanel() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("进程信息"));
        initComponents();
    }
    
    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // PID
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("PID:"), gbc);
        gbc.gridx = 1;
        pidLabel = new JLabel("-");
        add(pidLabel, gbc);
        
        // 进程名
        gbc.gridx = 2;
        add(new JLabel("名称:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        nameLabel = new JLabel("-");
        add(nameLabel, gbc);
        
        // CPU
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("CPU:"), gbc);
        gbc.gridx = 1;
        cpuLabel = new JLabel("-");
        add(cpuLabel, gbc);
        
        // 内存
        gbc.gridx = 2;
        add(new JLabel("内存:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        memLabel = new JLabel("-");
        add(memLabel, gbc);
        
        // 状态
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("状态:"), gbc);
        gbc.gridx = 1;
        statusLabel = new JLabel("-");
        add(statusLabel, gbc);
        
        // 用户
        gbc.gridx = 2;
        add(new JLabel("用户:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        userLabel = new JLabel("-");
        add(userLabel, gbc);
        
        // 命令行
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        add(new JLabel("命令:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        commandLabel = new JLabel("-");
        commandLabel.setToolTipText("");
        add(commandLabel, gbc);
    }
    
    public void setTask(TaskRow task) {
        this.currentTask = task;
        updateDisplay();
    }
    
    private void updateDisplay() {
        if (currentTask == null) {
            pidLabel.setText("-");
            nameLabel.setText("-");
            cpuLabel.setText("-");
            memLabel.setText("-");
            statusLabel.setText("-");
            userLabel.setText("-");
            commandLabel.setText("-");
            commandLabel.setToolTipText("");
        } else {
            pidLabel.setText(String.valueOf(currentTask.getPid()));
            nameLabel.setText(currentTask.getName());
            cpuLabel.setText(String.format("%.1f%%", currentTask.getCpuUsage()));
            memLabel.setText(formatMemory(currentTask.getMemoryUsage()));
            statusLabel.setText(currentTask.getStatus());
            userLabel.setText(currentTask.getUser());
            
            String cmd = currentTask.getCommand();
            commandLabel.setText(cmd);
            commandLabel.setToolTipText(cmd);
        }
    }
    
    private String formatMemory(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
    
    public TaskRow getCurrentTask() {
        return currentTask;
    }
}
