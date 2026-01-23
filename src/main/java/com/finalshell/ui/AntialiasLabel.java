package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 抗锯齿标签
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AntialiasLabel extends JLabel {
    
    public AntialiasLabel() {
        super();
    }
    
    public AntialiasLabel(String text) {
        super(text);
    }
    
    public AntialiasLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }
    
    public AntialiasLabel(Icon icon) {
        super(icon);
    }
    
    public AntialiasLabel(Icon icon, int horizontalAlignment) {
        super(icon, horizontalAlignment);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponent(g2);
        g2.dispose();
    }
}
