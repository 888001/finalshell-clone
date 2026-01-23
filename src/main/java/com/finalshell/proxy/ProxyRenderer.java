package com.finalshell.proxy;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 代理渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 3 && "是".equals(value)) {
            setForeground(new Color(0, 150, 0));
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setForeground(Color.BLACK);
            setFont(getFont().deriveFont(Font.PLAIN));
        }
        
        return this;
    }
}
