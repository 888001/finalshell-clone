package com.finalshell.portforward;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 端口转发渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFRenderer extends DefaultTableCellRenderer {
    
    private static final Color RUNNING_COLOR = new Color(0, 150, 0);
    private static final Color STOPPED_COLOR = new Color(150, 150, 150);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 4) {
            String status = (String) value;
            if ("运行中".equals(status)) {
                setForeground(RUNNING_COLOR);
            } else {
                setForeground(STOPPED_COLOR);
            }
        } else {
            setForeground(Color.BLACK);
        }
        
        return this;
    }
}
