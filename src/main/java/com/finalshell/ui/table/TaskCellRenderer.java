package com.finalshell.ui.table;

import com.finalshell.monitor.TopRow;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 任务单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TaskCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        TopRow topRow = (TopRow) value;
        
        if (topRow != null) {
            TaskTableModel model = (TaskTableModel) table.getModel();
            boolean forNetMon = model.isForNetMon();
            
            if (forNetMon) {
                switch (column) {
                    case 0: setText(String.valueOf(topRow.getPid())); break;
                    case 1: setText(topRow.getName()); break;
                    case 2: setText(String.format("%.1f", topRow.getCpu())); break;
                    case 3: setText(String.format("%.1f", topRow.getMem())); break;
                    case 4: setText(String.valueOf(topRow.getConnections())); break;
                }
            } else {
                switch (column) {
                    case 0: setText(String.valueOf(topRow.getPid())); break;
                    case 1: setText(topRow.getUser()); break;
                    case 2: setText(topRow.getName()); break;
                    case 3: setText(String.format("%.1f", topRow.getCpu())); break;
                    case 4: setText(String.format("%.1f", topRow.getMem())); break;
                    case 5: setText(topRow.getStatus()); break;
                    case 6: setText(topRow.getCommand()); break;
                }
            }
            
            // 数值列右对齐
            if (column == 0 || column == 3 || column == 4 || (forNetMon && column == 2)) {
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
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
