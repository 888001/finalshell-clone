package com.finalshell.ui.table;

import com.finalshell.monitor.DiskInfo;
import com.finalshell.ui.FormatTools;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 磁盘使用率单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class DFCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color warningColor = new Color(255, 150, 50);
    private Color dangerColor = new Color(220, 50, 50);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        DiskInfo info = (DiskInfo) value;
        
        if (info != null) {
            switch (column) {
                case 0: 
                    setText(info.getFileSystem());
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 1: 
                    setText(FormatTools.formatFileSize(info.getTotalSize()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 2: 
                    setText(FormatTools.formatFileSize(info.getUsedSize()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 3: 
                    setText(FormatTools.formatFileSize(info.getAvailableSize()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 4: 
                    double percent = info.getUsagePercent();
                    setText(String.format("%.1f%%", percent));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    if (percent >= 90) {
                        setForeground(dangerColor);
                    } else if (percent >= 80) {
                        setForeground(warningColor);
                    } else {
                        setForeground(Color.BLACK);
                    }
                    break;
                case 5: 
                    setText(info.getMountPoint());
                    setHorizontalAlignment(SwingConstants.LEFT);
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
}
