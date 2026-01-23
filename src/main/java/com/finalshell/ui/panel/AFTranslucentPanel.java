package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;

/**
 * 半透明面板
 * 支持透明度设置的面板组件
 */
public class AFTranslucentPanel extends JPanel {
    
    private float alpha = 1.0f;
    private Color backgroundColor = new Color(255, 255, 255, 200);
    
    public AFTranslucentPanel() {
        setOpaque(false);
    }
    
    public AFTranslucentPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
    
    public void setAlpha(float alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
        repaint();
    }
    
    public float getAlpha() { return alpha; }
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }
}
