package com.finalshell.sftp;

import javax.swing.*;
import java.awt.event.*;

/**
 * 传输任务右键菜单
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TransPopupMenu extends JPopupMenu {
    
    private JMenuItem pauseItem;
    private JMenuItem resumeItem;
    private JMenuItem cancelItem;
    private JMenuItem retryItem;
    private JMenuItem openFolderItem;
    private JMenuItem clearItem;
    private MenuListener listener;
    
    public TransPopupMenu() {
        initUI();
    }
    
    private void initUI() {
        pauseItem = new JMenuItem("暂停");
        pauseItem.addActionListener(e -> {
            if (listener != null) listener.onPause();
        });
        
        resumeItem = new JMenuItem("继续");
        resumeItem.addActionListener(e -> {
            if (listener != null) listener.onResume();
        });
        
        cancelItem = new JMenuItem("取消");
        cancelItem.addActionListener(e -> {
            if (listener != null) listener.onCancel();
        });
        
        retryItem = new JMenuItem("重试");
        retryItem.addActionListener(e -> {
            if (listener != null) listener.onRetry();
        });
        
        openFolderItem = new JMenuItem("打开文件夹");
        openFolderItem.addActionListener(e -> {
            if (listener != null) listener.onOpenFolder();
        });
        
        clearItem = new JMenuItem("清除");
        clearItem.addActionListener(e -> {
            if (listener != null) listener.onClear();
        });
        
        add(pauseItem);
        add(resumeItem);
        addSeparator();
        add(cancelItem);
        add(retryItem);
        addSeparator();
        add(openFolderItem);
        addSeparator();
        add(clearItem);
    }
    
    public void setMenuListener(MenuListener listener) {
        this.listener = listener;
    }
    
    public void updateForTask(TransferTask task) {
        if (task == null) return;
        
        boolean isRunning = task.getStatus() == TransferTask.Status.RUNNING;
        boolean isPaused = task.getStatus() == TransferTask.Status.PAUSED;
        boolean isCompleted = task.getStatus() == TransferTask.Status.COMPLETED;
        boolean isFailed = task.getStatus() == TransferTask.Status.FAILED;
        
        pauseItem.setEnabled(isRunning);
        resumeItem.setEnabled(isPaused);
        cancelItem.setEnabled(isRunning || isPaused);
        retryItem.setEnabled(isFailed);
        clearItem.setEnabled(isCompleted || isFailed);
    }
    
    public interface MenuListener {
        void onPause();
        void onResume();
        void onCancel();
        void onRetry();
        void onOpenFolder();
        void onClear();
    }
}
