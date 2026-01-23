package com.finalshell.network;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

/**
 * 速度测试画布
 * 绘制速度仪表盘和实时速度曲线
 */
public class SpeedTestCanvas extends JPanel {
    
    private double currentSpeed;
    private double maxSpeed;
    private double[] speedHistory;
    private int historyIndex;
    private int historySize;
    
    private Color bgColor;
    private Color fgColor;
    private Color speedColor;
    private Color maxSpeedColor;
    private Color gridColor;
    
    public SpeedTestCanvas() {
        this.historySize = 60;
        this.speedHistory = new double[historySize];
        this.historyIndex = 0;
        
        this.bgColor = new Color(30, 30, 30);
        this.fgColor = Color.WHITE;
        this.speedColor = new Color(0, 200, 100);
        this.maxSpeedColor = new Color(255, 100, 100);
        this.gridColor = new Color(60, 60, 60);
        
        setPreferredSize(new Dimension(300, 200));
        setBackground(bgColor);
    }
    
    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
        speedHistory[historyIndex] = speed;
        historyIndex = (historyIndex + 1) % historySize;
        if (speed > maxSpeed) {
            maxSpeed = speed;
        }
        repaint();
    }
    
    public void reset() {
        currentSpeed = 0;
        maxSpeed = 0;
        speedHistory = new double[historySize];
        historyIndex = 0;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, width, height);
        
        drawSpeedGauge(g2d, width / 4, height / 2, Math.min(width, height) / 3);
        drawSpeedChart(g2d, width / 2 + 20, 20, width / 2 - 40, height - 40);
        
        g2d.dispose();
    }
    
    private void drawSpeedGauge(Graphics2D g2d, int cx, int cy, int radius) {
        g2d.setColor(gridColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2));
        
        double displayMax = Math.max(maxSpeed, 10);
        double angle = 225 - (currentSpeed / displayMax) * 270;
        angle = Math.max(-45, Math.min(225, angle));
        
        g2d.setColor(speedColor);
        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(new Arc2D.Double(cx - radius + 10, cy - radius + 10, 
                (radius - 10) * 2, (radius - 10) * 2, 
                225, -(225 - angle), Arc2D.OPEN));
        
        g2d.setColor(fgColor);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        String speedStr = String.format("%.1f", currentSpeed);
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(speedStr, cx - fm.stringWidth(speedStr) / 2, cy + 5);
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g2d.drawString("MB/s", cx - fm.stringWidth("MB/s") / 2 + 5, cy + 20);
    }
    
    private void drawSpeedChart(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(gridColor);
        g2d.drawRect(x, y, width, height);
        
        for (int i = 1; i < 4; i++) {
            int gridY = y + (height * i) / 4;
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2, 2}, 0));
            g2d.drawLine(x, gridY, x + width, gridY);
        }
        
        double displayMax = Math.max(maxSpeed, 10);
        
        g2d.setColor(speedColor);
        g2d.setStroke(new BasicStroke(2));
        
        int[] xPoints = new int[historySize];
        int[] yPoints = new int[historySize];
        
        for (int i = 0; i < historySize; i++) {
            int idx = (historyIndex + i) % historySize;
            xPoints[i] = x + (width * i) / (historySize - 1);
            yPoints[i] = y + height - (int) ((speedHistory[idx] / displayMax) * height);
        }
        
        g2d.drawPolyline(xPoints, yPoints, historySize);
        
        if (maxSpeed > 0) {
            g2d.setColor(maxSpeedColor);
            int maxY = y + height - (int) ((maxSpeed / displayMax) * height);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 4}, 0));
            g2d.drawLine(x, maxY, x + width, maxY);
            
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2d.drawString(String.format("Max: %.1f", maxSpeed), x + 5, maxY - 3);
        }
    }
    
    public void setSpeedColor(Color color) {
        this.speedColor = color;
        repaint();
    }
    
    public void setMaxSpeedColor(Color color) {
        this.maxSpeedColor = color;
        repaint();
    }
    
    public double getCurrentSpeed() {
        return currentSpeed;
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }
}
