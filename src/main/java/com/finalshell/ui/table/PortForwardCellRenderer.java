package com.finalshell.ui.table;

import com.finalshell.config.PortForwardConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 端口转发单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class PortForwardCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    private Color activeColor = new Color(50, 150, 50);
    private Color inactiveColor = new Color(150, 150, 150);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        PortForwardConfig config = (PortForwardConfig) value;
        
        if (config != null) {
            switch (column) {
                case 0:
                    setText(config.isLocalToRemote() ? "本地→远程" : "远程→本地");
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 1:
                    setText(String.valueOf(config.getLocalPort()));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 2:
                    setText(config.getRemoteHost());
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.LEFT);
                    break;
                case 3:
                    setText(String.valueOf(config.getRemotePort()));
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.CENTER);
                    break;
                case 4:
                    boolean active = config.isActive();
                    setText(active ? "运行中" : "已停止");
                    setForeground(active ? activeColor : inactiveColor);
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
