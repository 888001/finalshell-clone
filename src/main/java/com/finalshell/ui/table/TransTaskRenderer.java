package com.finalshell.ui.table;

import com.finalshell.transfer.TransTask;
import com.finalshell.ui.FormatTools;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 传输任务渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TransTaskRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color errorColor = new Color(200, 50, 50);
    private Color successColor = new Color(50, 150, 50);
    private Color runningColor = new Color(50, 100, 200);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        TransTask task = (TransTask) value;
        
        if (task != null) {
            switch (column) {
                case 0: 
                    setText(task.getFileName());
                    break;
                case 1: 
                    setText(FormatTools.formatFileSize(task.getFileSize()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 2: 
                    setText(String.format("%.1f%%", task.getProgress() * 100));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 3: 
                    setText(getStatusText(task.getStatus()));
                    setForeground(getStatusColor(task.getStatus()));
                    break;
                case 4: 
                    if (task.getStatus() == TransTask.STATUS_RUNNING) {
                        setText(FormatTools.formatSpeed(task.getSpeed()));
                    } else {
                        setText("-");
                    }
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
            }
        } else {
            setText("");
        }
        
        if (isSelected) {
            setBackground(selectedBg);
        } else {
            setBackground(row % 2 == 0 ? normalBg : alternateBg);
        }
        
        return this;
    }
    
    private String getStatusText(int status) {
        switch (status) {
            case TransTask.STATUS_WAITING: return "等待中";
            case TransTask.STATUS_RUNNING: return "传输中";
            case TransTask.STATUS_SUCCESS: return "完成";
            case TransTask.STATUS_ERROR: return "错误";
            case TransTask.STATUS_CANCEL: return "已取消";
            case TransTask.STATUS_PAUSE: return "已暂停";
            default: return "未知";
        }
    }
    
    private Color getStatusColor(int status) {
        switch (status) {
            case TransTask.STATUS_RUNNING: return runningColor;
            case TransTask.STATUS_SUCCESS: return successColor;
            case TransTask.STATUS_ERROR: return errorColor;
            default: return Color.BLACK;
        }
    }
}
