package com.finalshell.ui.panel;

import com.finalshell.transfer.TransTask;
import com.finalshell.ui.table.TransTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 传输主面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md
 */
public class TransePanel extends JPanel {
    
    private TransTable transTable;
    private JButton clearButton;
    private JButton cancelButton;
    private JButton pauseButton;
    private JLabel statusLabel;
    private JProgressBar totalProgress;
    
    public TransePanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // 工具栏
        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        clearButton = new JButton("清除已完成");
        clearButton.addActionListener(e -> clearCompleted());
        toolBar.add(clearButton);
        
        cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> cancelSelected());
        toolBar.add(cancelButton);
        
        pauseButton = new JButton("暂停");
        pauseButton.addActionListener(e -> pauseSelected());
        toolBar.add(pauseButton);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 传输表格
        transTable = new TransTable();
        JScrollPane scrollPane = new JScrollPane(transTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 状态栏
        JPanel statusBar = new JPanel(new BorderLayout(10, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        statusLabel = new JLabel("就绪");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        totalProgress = new JProgressBar(0, 100);
        totalProgress.setStringPainted(true);
        totalProgress.setPreferredSize(new Dimension(200, 20));
        statusBar.add(totalProgress, BorderLayout.EAST);
        
        add(statusBar, BorderLayout.SOUTH);
        
        // 右键菜单
        JPopupMenu popupMenu = createPopupMenu();
        transTable.setComponentPopupMenu(popupMenu);
    }
    
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem cancelItem = new JMenuItem("取消");
        cancelItem.addActionListener(e -> cancelSelected());
        menu.add(cancelItem);
        
        JMenuItem pauseItem = new JMenuItem("暂停");
        pauseItem.addActionListener(e -> pauseSelected());
        menu.add(pauseItem);
        
        JMenuItem resumeItem = new JMenuItem("继续");
        resumeItem.addActionListener(e -> resumeSelected());
        menu.add(resumeItem);
        
        menu.addSeparator();
        
        JMenuItem clearItem = new JMenuItem("清除已完成");
        clearItem.addActionListener(e -> clearCompleted());
        menu.add(clearItem);
        
        return menu;
    }
    
    public void addTask(TransTask task) {
        transTable.addTask(task);
        updateStatus();
    }
    
    public void removeTask(TransTask task) {
        transTable.removeTask(task);
        updateStatus();
    }
    
    public void updateTask(TransTask task) {
        transTable.updateTask(task);
        updateStatus();
    }
    
    private void clearCompleted() {
        transTable.getTransModel().clearCompleted();
        updateStatus();
    }
    
    private void cancelSelected() {
        for (TransTask task : transTable.getSelectedTasks()) {
            task.cancel();
        }
    }
    
    private void pauseSelected() {
        for (TransTask task : transTable.getSelectedTasks()) {
            task.pause();
        }
    }
    
    private void resumeSelected() {
        for (TransTask task : transTable.getSelectedTasks()) {
            task.resume();
        }
    }
    
    private void updateStatus() {
        int total = transTable.getTransModel().getRowCount();
        int active = transTable.countWaitingAndRunning();
        statusLabel.setText(String.format("任务: %d / 活动: %d", total, active));
    }
    
    public TransTable getTransTable() {
        return transTable;
    }
}
