package com.finalshell.ui.table;

import com.finalshell.network.TracertHop;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 路由跟踪单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TracertCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color timeoutColor = new Color(180, 180, 180);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        TracertHop hop = (TracertHop) value;
        
        if (hop != null) {
            boolean timeout = hop.isTimeout();
            
            switch (column) {
                case 0: 
                    setText(String.valueOf(hop.getHopNumber()));
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 1: 
                    setText(timeout ? "*" : hop.getIpAddress());
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 2: 
                    setText(timeout ? "*" : hop.getHostname());
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 3: 
                    if (timeout) {
                        setText("*");
                    } else {
                        setText(String.format("%.1f ms", hop.getLatency()));
                    }
                    setHorizontalAlignment(SwingConstants.RIGHT);
                    break;
                case 4: 
                    setText(hop.getLocation() != null ? hop.getLocation() : "-");
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
            }
            
            if (timeout) {
                setForeground(timeoutColor);
            } else {
                setForeground(Color.BLACK);
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
