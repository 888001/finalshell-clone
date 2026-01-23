package com.finalshell.process;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 环境变量表格渲染器
 * 用于渲染进程环境变量表格的单元格
 */
public class EnvTableRenderer extends DefaultTableCellRenderer {
    
    private static final long serialVersionUID = 1L;
    
    private Color alternateRowColor = new Color(245, 245, 250);
    private Color selectedBgColor = new Color(51, 153, 255);
    private Color selectedFgColor = Color.WHITE;
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (isSelected) {
            setBackground(selectedBgColor);
            setForeground(selectedFgColor);
        } else {
            setForeground(table.getForeground());
            if (row % 2 == 0) {
                setBackground(table.getBackground());
            } else {
                setBackground(alternateRowColor);
            }
        }
        
        // 设置文本
        if (value != null) {
            setText(value.toString());
            setToolTipText(value.toString());
        } else {
            setText("");
            setToolTipText(null);
        }
        
        // 根据列调整对齐方式
        if (column == 0) {
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setHorizontalAlignment(SwingConstants.LEFT);
            setFont(getFont().deriveFont(Font.PLAIN));
        }
        
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        return this;
    }
    
    public void setAlternateRowColor(Color color) {
        this.alternateRowColor = color;
    }
    
    public void setSelectedBgColor(Color color) {
        this.selectedBgColor = color;
    }
    
    public void setSelectedFgColor(Color color) {
        this.selectedFgColor = color;
    }
}
