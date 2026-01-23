package com.finalshell.ui.table;

import com.finalshell.hotkey.HotkeyConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 快捷键单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class HotkeyCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color hotkeyColor = new Color(80, 80, 180);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        HotkeyConfig config = (HotkeyConfig) value;
        
        if (config != null) {
            switch (column) {
                case 0:
                    setText(config.getActionName());
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 1:
                    setText(config.getKeyStrokeString());
                    setForeground(hotkeyColor);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 2:
                    setText("编辑");
                    setForeground(Color.BLUE);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
            }
        } else {
            setText("");
        }
        
        if (isSelected) {
            setBackground(selectedBg);
        } else {
            setBackground(row % 2 == 0 ? normalBg : alternateBg);
        }
        
        return this;
    }
}
