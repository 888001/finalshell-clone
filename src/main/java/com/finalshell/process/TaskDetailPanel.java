package com.finalshell.process;

import javax.swing.*;
import java.awt.*;
import com.finalshell.monitor.TaskInfo;

/**
 * 任务详情面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TaskDetailPanel extends JPanel {
    
    private JLabel pidLabel;
    private JLabel nameLabel;
    private JLabel userLabel;
    private JLabel cpuLabel;
    private JLabel memLabel;
    private JLabel statusLabel;
    private JLabel commandLabel;
    private JTextArea detailArea;
    
    public TaskDetailPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("进程详情"));
        
        JPanel infoPanel = new JPanel(new GridLayout(4, 4, 10, 5));
        
        infoPanel.add(new JLabel("PID:"));
        pidLabel = new JLabel("-");
        infoPanel.add(pidLabel);
        
        infoPanel.add(new JLabel("名称:"));
        nameLabel = new JLabel("-");
        infoPanel.add(nameLabel);
        
        infoPanel.add(new JLabel("用户:"));
        userLabel = new JLabel("-");
        infoPanel.add(userLabel);
        
        infoPanel.add(new JLabel("状态:"));
        statusLabel = new JLabel("-");
        infoPanel.add(statusLabel);
        
        infoPanel.add(new JLabel("CPU:"));
        cpuLabel = new JLabel("-");
        infoPanel.add(cpuLabel);
        
        infoPanel.add(new JLabel("内存:"));
        memLabel = new JLabel("-");
        infoPanel.add(memLabel);
        
        infoPanel.add(new JLabel("命令:"));
        commandLabel = new JLabel("-");
        infoPanel.add(commandLabel);
        
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(detailArea);
        
        add(infoPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void showTask(TaskInfo task) {
        if (task == null) {
            clear();
            return;
        }
        
        pidLabel.setText(String.valueOf(task.getPid()));
        nameLabel.setText(task.getName());
        userLabel.setText(task.getUser());
        cpuLabel.setText(String.format("%.1f%%", task.getCpu()));
        memLabel.setText(String.format("%.1f%%", task.getMem()));
        statusLabel.setText(task.getStatus());
        commandLabel.setText(task.getCommand());
        
        StringBuilder detail = new StringBuilder();
        detail.append("PID: ").append(task.getPid()).append("\n");
        detail.append("名称: ").append(task.getName()).append("\n");
        detail.append("用户: ").append(task.getUser()).append("\n");
        detail.append("CPU: ").append(String.format("%.1f%%", task.getCpu())).append("\n");
        detail.append("内存: ").append(String.format("%.1f%%", task.getMem())).append("\n");
        detail.append("VSZ: ").append(task.getVsz()).append("\n");
        detail.append("RSS: ").append(task.getRss()).append("\n");
        detail.append("状态: ").append(task.getStatus()).append("\n");
        detail.append("命令: ").append(task.getCommand()).append("\n");
        
        detailArea.setText(detail.toString());
    }
    
    public void clear() {
        pidLabel.setText("-");
        nameLabel.setText("-");
        userLabel.setText("-");
        cpuLabel.setText("-");
        memLabel.setText("-");
        statusLabel.setText("-");
        commandLabel.setText("-");
        detailArea.setText("");
    }
    
    public void setTask(TaskInfo task) {
        showTask(task);
    }
}
