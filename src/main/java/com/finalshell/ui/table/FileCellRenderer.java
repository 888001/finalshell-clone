package com.finalshell.ui.table;

import com.finalshell.sftp.RemoteFile;
import com.finalshell.ui.FormatTools;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;

/**
 * 文件列表单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class FileCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color dirColor = new Color(50, 50, 150);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        RemoteFile file = (RemoteFile) value;
        
        if (file != null) {
            switch (column) {
                case 0:
                    setText(file.getName());
                    if (file.isDirectory()) {
                        setForeground(dirColor);
                    } else {
                        setForeground(Color.BLACK);
                    }
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 1:
                    if (file.isDirectory()) {
                        setText("-");
                    } else {
                        setText(FormatTools.formatFileSize(file.getSize()));
                    }
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 2:
                    setText(file.isDirectory() ? "文件夹" : getFileType(file.getName()));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 3:
                    if (file.getModifiedTime() > 0) {
                        setText(dateFormat.format(new java.util.Date(file.getModifiedTime())));
                    } else {
                        setText("-");
                    }
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 4:
                    setText(file.getPermissions() != null ? file.getPermissions() : "-");
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
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
    
    private String getFileType(String name) {
        if (name == null) return "文件";
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < name.length() - 1) {
            return name.substring(dotIndex + 1).toUpperCase();
        }
        return "文件";
    }
}
