package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Traceroute表格单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertCellRenderer extends DefaultTableCellRenderer {
    
    private static final Color FAST_COLOR = new Color(50, 150, 50);
    private static final Color MEDIUM_COLOR = new Color(200, 150, 50);
    private static final Color SLOW_COLOR = new Color(200, 50, 50);
    private static final Color TIMEOUT_COLOR = Color.GRAY;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column >= 3 && column <= 5 && value != null) {
            String strValue = value.toString();
            if ("*".equals(strValue)) {
                setForeground(TIMEOUT_COLOR);
            } else {
                try {
                    long rtt = Long.parseLong(strValue.replace(" ms", "").trim());
                    if (rtt < 50) {
                        setForeground(FAST_COLOR);
                    } else if (rtt < 200) {
                        setForeground(MEDIUM_COLOR);
                    } else {
                        setForeground(SLOW_COLOR);
                    }
                } catch (NumberFormatException e) {
                    setForeground(Color.BLACK);
                }
            }
        } else {
            if (!isSelected) {
                setForeground(Color.BLACK);
            }
        }
        
        if (column == 0) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else if (column >= 3) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return this;
    }
}
