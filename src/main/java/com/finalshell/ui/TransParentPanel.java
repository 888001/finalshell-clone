package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 透明面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - TransParentPanel
 */
public class TransParentPanel extends JPanel {
    
    private float alpha = 0.5f;
    private Color overlayColor = new Color(0, 0, 0, 128);
    
    public TransParentPanel() {
        setOpaque(false);
    }
    
    public TransParentPanel(float alpha) {
        this.alpha = alpha;
        setOpaque(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(overlayColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
    
    public float getAlpha() { return alpha; }
    public void setAlpha(float alpha) { 
        this.alpha = alpha;
        repaint();
    }
    
    public Color getOverlayColor() { return overlayColor; }
    public void setOverlayColor(Color color) { 
        this.overlayColor = color;
        repaint();
    }
}
