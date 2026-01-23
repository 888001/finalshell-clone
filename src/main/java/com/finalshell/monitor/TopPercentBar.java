package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * Top进程百分比条
 * 用于Top面板中显示CPU/内存使用率
 */
public class TopPercentBar extends JComponent {
    
    private double value = 0;
    private Color lowColor = new Color(76, 175, 80);
    private Color midColor = new Color(255, 193, 7);
    private Color highColor = new Color(244, 67, 54);
    
    public TopPercentBar() {
        setPreferredSize(new Dimension(60, 14));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        g2.setColor(new Color(230, 230, 230));
        g2.fillRoundRect(0, 0, w, h, 3, 3);
        
        double percent = Math.min(value / 100.0, 1.0);
        int barWidth = (int) (w * percent);
        if (barWidth > 0) {
            Color barColor;
            if (value < 50) barColor = lowColor;
            else if (value < 80) barColor = midColor;
            else barColor = highColor;
            
            g2.setColor(barColor);
            g2.fillRoundRect(0, 0, barWidth, h, 3, 3);
        }
        
        g2.dispose();
    }
    
    public void setValue(double value) {
        this.value = value;
        repaint();
    }
    
    public double getValue() { return value; }
}
