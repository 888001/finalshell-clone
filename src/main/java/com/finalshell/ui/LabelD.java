package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义标签
 * 支持双缓冲和抗锯齿渲染
 */
public class LabelD extends JLabel {
    
    private boolean antialiasing = true;
    
    public LabelD() {
        super();
        setDoubleBuffered(true);
    }
    
    public LabelD(String text) {
        super(text);
        setDoubleBuffered(true);
    }
    
    public LabelD(Icon image) {
        super(image);
        setDoubleBuffered(true);
    }
    
    public LabelD(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setDoubleBuffered(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        if (antialiasing) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        }
        super.paintComponent(g);
    }
    
    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
        repaint();
    }
    
    public boolean isAntialiasing() {
        return antialiasing;
    }
}
