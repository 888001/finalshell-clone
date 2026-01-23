package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 传输面板包装器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TransPanelWrap extends JPanel {
    
    private TransePanel transPanel;
    private JToolBar toolbar;
    private JButton pauseAllButton;
    private JButton resumeAllButton;
    private JButton clearButton;
    private JLabel statusLabel;
    
    public TransPanelWrap() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        pauseAllButton = new JButton("全部暂停");
        pauseAllButton.addActionListener(e -> pauseAll());
        
        resumeAllButton = new JButton("全部继续");
        resumeAllButton.addActionListener(e -> resumeAll());
        
        clearButton = new JButton("清除已完成");
        clearButton.addActionListener(e -> clearCompleted());
        
        statusLabel = new JLabel("0 个任务");
        
        toolbar.add(pauseAllButton);
        toolbar.add(resumeAllButton);
        toolbar.addSeparator();
        toolbar.add(clearButton);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(statusLabel);
        
        transPanel = new TransePanel();
        
        add(toolbar, BorderLayout.NORTH);
        add(transPanel, BorderLayout.CENTER);
    }
    
    private void pauseAll() {
        transPanel.pauseAllTasks();
        updateStatus();
    }
    
    private void resumeAll() {
        transPanel.resumeAllTasks();
        updateStatus();
    }
    
    private void clearCompleted() {
        transPanel.clearCompletedTasks();
        updateStatus();
    }
    
    public void updateStatus() {
        int count = transPanel.getTaskCount();
        statusLabel.setText(count + " 个任务");
    }
    
    public TransePanel getTransPanel() {
        return transPanel;
    }
}
