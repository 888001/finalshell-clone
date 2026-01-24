package com.finalshell.ui.table;

import com.finalshell.network.NetRow;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 网络单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class NetCellRenderer extends DefaultTableCellRenderer {
    
    private Color normalBg = Color.WHITE;
    private Color alternateBg = new Color(245, 245, 250);
    private Color selectedBg = new Color(200, 220, 240);
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        
        NetRow netRow = (NetRow) value;
        
        if (netRow != null) {
            switch (column) {
                case 0: setText(netRow.getProtocol()); break;
                case 1: setText(formatAddress(netRow.getLocalAddress())); break;
                case 2: setText(String.valueOf(netRow.getLocalPort())); break;
                case 3: setText(formatAddress(netRow.getRemoteAddress())); break;
                case 4: setText(String.valueOf(netRow.getRemotePort())); break;
                case 5: setText(netRow.getState()); break;
            }
            
            // 端口列右对齐
            if (column == 2 || column == 4) {
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
    
    private String formatAddress(String address) {
        if (address == null) return "";
        // 处理IPv6映射地址
        if (address.startsWith("::ffff:")) {
            return address.substring(7);
        }
        return address;
    }
}
