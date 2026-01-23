package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * 百分比进度条
 * 自定义绘制的百分比显示组件
 */
public class PercentBar extends JComponent {
    
    private double value = 0;
    private double maxValue = 100;
    private Color barColor = new Color(51, 153, 255);
    private Color bgColor = new Color(230, 230, 230);
    private Color textColor = Color.BLACK;
    private boolean showText = true;
    private String format = "%.1f%%";
    
    public PercentBar() {
        setPreferredSize(new Dimension(100, 20));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, 4, 4);
        
        double percent = Math.min(value / maxValue, 1.0);
        int barWidth = (int) (w * percent);
        if (barWidth > 0) {
            g2.setColor(barColor);
            g2.fillRoundRect(0, 0, barWidth, h, 4, 4);
        }
        
        if (showText) {
            String text = String.format(format, value);
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (w - fm.stringWidth(text)) / 2;
            int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
        
        g2.dispose();
    }
    
    public void setValue(double value) {
        this.value = value;
        repaint();
    }
    
    public double getValue() { return value; }
    public void setMaxValue(double max) { this.maxValue = max; }
    public void setBarColor(Color color) { this.barColor = color; repaint(); }
    public void setShowText(boolean show) { this.showText = show; repaint(); }
    public void setFormat(String format) { this.format = format; }
}
