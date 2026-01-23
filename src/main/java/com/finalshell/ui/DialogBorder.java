package com.finalshell.ui;

import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * 自定义对话框边框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - DialogBorder
 */
public class DialogBorder extends AbstractBorder {
    
    private Color borderColor;
    private int thickness;
    private int radius;
    
    public DialogBorder() {
        this(UIConfig.BORDER_GRAY, 1, 5);
    }
    
    public DialogBorder(Color color, int thickness, int radius) {
        this.borderColor = color;
        this.thickness = thickness;
        this.radius = radius;
    }
    
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(thickness));
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }
    
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.top = insets.bottom = thickness + 2;
        return insets;
    }
    
    public Color getBorderColor() { return borderColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    
    public int getThickness() { return thickness; }
    public void setThickness(int thickness) { this.thickness = thickness; }
    
    public int getRadius() { return radius; }
    public void setRadius(int radius) { this.radius = radius; }
}
