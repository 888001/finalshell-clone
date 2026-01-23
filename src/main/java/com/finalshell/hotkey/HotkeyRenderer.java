package com.finalshell.hotkey;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 快捷键渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class HotkeyRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (!isSelected) {
            setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
        }
        
        if (value instanceof KeyStroke) {
            KeyStroke ks = (KeyStroke) value;
            setText(ks.toString().replace("pressed ", ""));
        }
        
        if (column == 2) {
            setFont(getFont().deriveFont(Font.BOLD));
            setForeground(new Color(0, 100, 150));
        } else {
            setForeground(Color.BLACK);
        }
        
        return this;
    }
}
