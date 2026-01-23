package com.finalshell.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * 透明表头
 * 自定义透明效果的表格表头
 */
public class TransparentHeader extends JTableHeader {
    
    private float alpha = 0.8f;
    private Color headerColor = new Color(240, 240, 240);
    
    public TransparentHeader(TableColumnModel columnModel) {
        super(columnModel);
        setOpaque(false);
        setDefaultRenderer(new TransparentHeaderRenderer());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(headerColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
        repaint();
    }
    
    public void setHeaderColor(Color color) {
        this.headerColor = color;
        repaint();
    }
    
    private class TransparentHeaderRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(false);
            setHorizontalAlignment(CENTER);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.LIGHT_GRAY));
            return this;
        }
    }
}
