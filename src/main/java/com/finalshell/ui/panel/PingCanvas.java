package com.finalshell.ui.panel;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * Ping图形画布
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class PingCanvas extends JPanel {
    
    private java.util.List<Integer> values = new ArrayList<>();
    private int maxPoints = 50;
    private int maxValue = 100;
    
    private Color bgColor = new Color(30, 30, 40);
    private Color gridColor = new Color(60, 60, 70);
    private Color lineColor = new Color(0, 200, 100);
    private Color pointColor = new Color(0, 255, 150);
    
    public PingCanvas() {
        setBackground(bgColor);
    }
    
    public void addValue(int value) {
        values.add(value);
        if (values.size() > maxPoints) {
            values.remove(0);
        }
        if (value > maxValue) {
            maxValue = value + 50;
        }
        repaint();
    }
    
    public void clear() {
        values.clear();
        maxValue = 100;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 30;
        int graphWidth = width - padding * 2;
        int graphHeight = height - padding * 2;
        
        // 绘制背景
        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);
        
        // 绘制网格
        g2.setColor(gridColor);
        for (int i = 0; i <= 4; i++) {
            int y = padding + (graphHeight * i / 4);
            g2.drawLine(padding, y, width - padding, y);
            
            int labelValue = maxValue - (maxValue * i / 4);
            g2.setColor(Color.GRAY);
            g2.drawString(labelValue + "ms", 5, y + 4);
            g2.setColor(gridColor);
        }
        
        // 绘制折线
        if (values.size() > 1) {
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(2));
            
            int[] xPoints = new int[values.size()];
            int[] yPoints = new int[values.size()];
            
            for (int i = 0; i < values.size(); i++) {
                xPoints[i] = padding + (graphWidth * i / (maxPoints - 1));
                yPoints[i] = padding + graphHeight - (graphHeight * values.get(i) / maxValue);
            }
            
            for (int i = 0; i < values.size() - 1; i++) {
                g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }
            
            // 绘制点
            g2.setColor(pointColor);
            for (int i = 0; i < values.size(); i++) {
                g2.fillOval(xPoints[i] - 3, yPoints[i] - 3, 6, 6);
            }
        }
        
        // 绘制边框
        g2.setColor(gridColor);
        g2.drawRect(padding, padding, graphWidth, graphHeight);
    }
}
