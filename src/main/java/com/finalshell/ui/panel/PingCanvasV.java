package com.finalshell.ui.panel;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Ping可视化画布
 * 用于绘制Ping响应时间的可视化图表
 */
public class PingCanvasV extends Canvas {
    
    private static final long serialVersionUID = 1L;
    
    private List<Long> pingData = new ArrayList<>();
    private int maxDataPoints = 100;
    private long maxLatency = 500;
    private Color successColor = new Color(100, 200, 100);
    private Color timeoutColor = new Color(250, 120, 100);
    private Color gridColor = new Color(200, 200, 200);
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // 绘制背景
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, width, height);
        
        // 绘制网格
        drawGrid(g2d, width, height);
        
        // 绘制Ping数据
        drawPingData(g2d, width, height);
    }
    
    private void drawGrid(Graphics2D g2d, int width, int height) {
        g2d.setColor(gridColor);
        
        // 水平线
        int gridLines = 5;
        for (int i = 1; i < gridLines; i++) {
            int y = height * i / gridLines;
            g2d.drawLine(0, y, width, y);
        }
    }
    
    private void drawPingData(Graphics2D g2d, int width, int height) {
        if (pingData.isEmpty()) {
            return;
        }
        
        int dataSize = pingData.size();
        float barWidth = (float) width / maxDataPoints;
        
        for (int i = 0; i < dataSize; i++) {
            long latency = pingData.get(i);
            int x = (int) (i * barWidth);
            
            if (latency <= 0) {
                // 超时
                g2d.setColor(timeoutColor);
                g2d.fillRect(x, 0, (int) barWidth, height);
            } else {
                // 正常响应
                g2d.setColor(successColor);
                int barHeight = (int) (height * Math.min(latency, maxLatency) / maxLatency);
                g2d.fillRect(x, height - barHeight, (int) barWidth, barHeight);
            }
        }
    }
    
    public void addPingData(long latency) {
        pingData.add(latency);
        if (pingData.size() > maxDataPoints) {
            pingData.remove(0);
        }
        repaint();
    }
    
    public void clearData() {
        pingData.clear();
        repaint();
    }
    
    public void setMaxDataPoints(int max) {
        this.maxDataPoints = max;
    }
    
    public void setMaxLatency(long max) {
        this.maxLatency = max;
    }
    
    public void setSuccessColor(Color color) {
        this.successColor = color;
    }
    
    public void setTimeoutColor(Color color) {
        this.timeoutColor = color;
    }
}
