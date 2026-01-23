package com.finalshell.monitor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 进程TOP单元格渲染器
 */
public class TopCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 2 || column == 3) {
            setHorizontalAlignment(RIGHT);
            if (value instanceof Number) {
                double v = ((Number) value).doubleValue();
                if (v > 80) setForeground(Color.RED);
                else if (v > 50) setForeground(Color.ORANGE);
                else setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
            }
        } else {
            setHorizontalAlignment(column == 0 ? RIGHT : LEFT);
            setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
        }
        
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return this;
    }
}
