package com.finalshell.command;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 命令渲染器
 * 自定义命令列表的单元格渲染
 */
public class CommandRender extends DefaultTableCellRenderer {
    
    private Color selectedBg = new Color(51, 153, 255);
    private Color selectedFg = Color.WHITE;
    private Color normalBg = Color.WHITE;
    private Color normalFg = Color.BLACK;
    private Color alternateBg = new Color(245, 245, 245);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        if (isSelected) {
            setBackground(selectedBg);
            setForeground(selectedFg);
        } else {
            setBackground(row % 2 == 0 ? normalBg : alternateBg);
            setForeground(normalFg);
        }
        
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        if (column == 0) {
            setFont(getFont().deriveFont(Font.BOLD));
        } else {
            setFont(getFont().deriveFont(Font.PLAIN));
            setForeground(isSelected ? selectedFg : Color.GRAY);
        }
        
        return this;
    }
    
    public void setSelectedBackground(Color color) { this.selectedBg = color; }
    public void setSelectedForeground(Color color) { this.selectedFg = color; }
}
