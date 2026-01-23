package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 套接字单元格渲染器
 */
public class SocketCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (column == 2 || column == 4) {
            setHorizontalAlignment(RIGHT);
        } else if (column == 5) {
            setHorizontalAlignment(CENTER);
            String state = value != null ? value.toString() : "";
            if ("ESTABLISHED".equals(state)) {
                setForeground(isSelected ? table.getSelectionForeground() : new Color(76, 175, 80));
            } else if ("LISTEN".equals(state)) {
                setForeground(isSelected ? table.getSelectionForeground() : new Color(33, 150, 243));
            } else if ("TIME_WAIT".equals(state) || "CLOSE_WAIT".equals(state)) {
                setForeground(isSelected ? table.getSelectionForeground() : Color.ORANGE);
            } else {
                setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
            }
        } else {
            setHorizontalAlignment(LEFT);
            setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
        }
        
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return this;
    }
}
