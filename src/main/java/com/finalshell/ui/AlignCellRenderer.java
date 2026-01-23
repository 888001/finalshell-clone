package com.finalshell.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 对齐单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Package_Analysis.md - AlignCellRenderer
 */
public class AlignCellRenderer extends DefaultTableCellRenderer {
    
    private int alignment;
    
    public AlignCellRenderer() {
        this(SwingConstants.LEFT);
    }
    
    public AlignCellRenderer(int alignment) {
        this.alignment = alignment;
        setHorizontalAlignment(alignment);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(alignment);
        return this;
    }
}
