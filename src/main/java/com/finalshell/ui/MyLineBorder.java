package com.finalshell.ui;

import javax.swing.border.*;
import java.awt.*;

/**
 * 自定义线边框
 * 支持圆角和自定义颜色
 */
public class MyLineBorder extends AbstractBorder {
    
    private Color color;
    private int thickness;
    private int radius;
    private boolean top, left, bottom, right;
    
    public MyLineBorder(Color color) {
        this(color, 1, 0);
    }
    
    public MyLineBorder(Color color, int thickness) {
        this(color, thickness, 0);
    }
    
    public MyLineBorder(Color color, int thickness, int radius) {
        this.color = color;
        this.thickness = thickness;
        this.radius = radius;
        this.top = this.left = this.bottom = this.right = true;
    }
    
    public MyLineBorder(Color color, int thickness, boolean top, boolean left, 
                        boolean bottom, boolean right) {
        this.color = color;
        this.thickness = thickness;
        this.radius = 0;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        
        if (radius > 0) {
            g2.drawRoundRect(x + thickness/2, y + thickness/2, 
                width - thickness, height - thickness, radius, radius);
        } else {
            if (top) g2.drawLine(x, y + thickness/2, x + width, y + thickness/2);
            if (left) g2.drawLine(x + thickness/2, y, x + thickness/2, y + height);
            if (bottom) g2.drawLine(x, y + height - thickness/2, x + width, y + height - thickness/2);
            if (right) g2.drawLine(x + width - thickness/2, y, x + width - thickness/2, y + height);
        }
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(top ? thickness : 0, left ? thickness : 0, 
                         bottom ? thickness : 0, right ? thickness : 0);
    }
    
    @Override
    public boolean isBorderOpaque() { return false; }
    
    public void setColor(Color color) { this.color = color; }
    public Color getColor() { return color; }
}
