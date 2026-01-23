package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 拖拽窗口块
 * 用于标签页拖拽时显示的区域指示器
 */
public class DragWinBlock extends JPanel {
    
    private Color blockColor = new Color(100, 150, 200, 100);
    private Color borderColor = new Color(50, 100, 150);
    private boolean highlighted = false;
    
    public DragWinBlock() {
        setOpaque(false);
        setPreferredSize(new Dimension(100, 50));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (highlighted) {
            g2.setColor(blockColor);
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 8, 8);
        }
        
        g2.dispose();
    }
    
    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        repaint();
    }
    
    public boolean isHighlighted() {
        return highlighted;
    }
    
    public void setBlockColor(Color color) {
        this.blockColor = color;
        repaint();
    }
    
    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }
}
