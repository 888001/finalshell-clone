package com.finalshell.portmap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 端口映射规则渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MapRuleRenderer extends DefaultTableCellRenderer {
    
    private static final Color RUNNING_COLOR = new Color(50, 150, 50);
    private static final Color STOPPED_COLOR = Color.GRAY;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 6 && value != null) {
            String status = value.toString();
            if ("运行中".equals(status)) {
                setForeground(RUNNING_COLOR);
            } else {
                setForeground(STOPPED_COLOR);
            }
        } else {
            if (!isSelected) {
                setForeground(Color.BLACK);
            }
        }
        
        if (column == 3 || column == 5) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        return this;
    }
}
