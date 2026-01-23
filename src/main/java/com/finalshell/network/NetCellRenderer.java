package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 网络表格单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetCellRenderer extends DefaultTableCellRenderer {
    
    private static final Color ESTABLISHED_COLOR = new Color(50, 150, 50);
    private static final Color LISTEN_COLOR = new Color(50, 50, 200);
    private static final Color CLOSE_WAIT_COLOR = new Color(200, 150, 50);
    private static final Color TIME_WAIT_COLOR = new Color(150, 150, 150);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 5 && value != null) {
            String state = value.toString();
            if ("ESTABLISHED".equalsIgnoreCase(state)) {
                setForeground(ESTABLISHED_COLOR);
            } else if ("LISTEN".equalsIgnoreCase(state)) {
                setForeground(LISTEN_COLOR);
            } else if ("CLOSE_WAIT".equalsIgnoreCase(state)) {
                setForeground(CLOSE_WAIT_COLOR);
            } else if ("TIME_WAIT".equalsIgnoreCase(state)) {
                setForeground(TIME_WAIT_COLOR);
            } else {
                setForeground(Color.BLACK);
            }
        } else {
            if (!isSelected) {
                setForeground(Color.BLACK);
            }
        }
        
        return this;
    }
}
