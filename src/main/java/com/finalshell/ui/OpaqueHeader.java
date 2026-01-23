package com.finalshell.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 不透明表头
 * 自定义不透明效果的表格表头
 */
public class OpaqueHeader extends JTableHeader {
    
    private Color headerColor = new Color(230, 230, 230);
    private Color borderColor = new Color(200, 200, 200);
    
    public OpaqueHeader(TableColumnModel columnModel) {
        super(columnModel);
        setOpaque(true);
        setBackground(headerColor);
        setDefaultRenderer(new OpaqueHeaderRenderer());
    }
    
    public void setHeaderColor(Color color) {
        this.headerColor = color;
        setBackground(color);
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
    
    private class OpaqueHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(true);
            setBackground(headerColor);
            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, borderColor));
            return this;
        }
    }
}
