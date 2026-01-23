package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 网络速度画布
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SpeedCanvas extends JPanel {
    
    private static final int MAX_POINTS = 60;
    private List<Double> rxData = new ArrayList<>();
    private List<Double> txData = new ArrayList<>();
    private double maxSpeed = 1024;
    
    private Color rxColor = new Color(50, 150, 50);
    private Color txColor = new Color(50, 50, 200);
    private Color gridColor = new Color(200, 200, 200);
    private Color bgColor = new Color(240, 240, 240);
    
    public SpeedCanvas() {
        setPreferredSize(new Dimension(200, 100));
        setBackground(bgColor);
    }
    
    public void addData(double rxSpeed, double txSpeed) {
        rxData.add(rxSpeed);
        txData.add(txSpeed);
        
        while (rxData.size() > MAX_POINTS) {
            rxData.remove(0);
        }
        while (txData.size() > MAX_POINTS) {
            txData.remove(0);
        }
        
        double currentMax = Math.max(
            rxData.stream().mapToDouble(Double::doubleValue).max().orElse(0),
            txData.stream().mapToDouble(Double::doubleValue).max().orElse(0)
        );
        
        if (currentMax > maxSpeed * 0.8) {
            maxSpeed = currentMax * 1.5;
        } else if (currentMax < maxSpeed * 0.3 && maxSpeed > 1024) {
            maxSpeed = Math.max(1024, currentMax * 2);
        }
        
        repaint();
    }
    
    public void clear() {
        rxData.clear();
        txData.clear();
        maxSpeed = 1024;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 5;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        g2.setColor(gridColor);
        for (int i = 1; i < 4; i++) {
            int y = padding + (chartHeight * i) / 4;
            g2.drawLine(padding, y, width - padding, y);
        }
        
        if (!rxData.isEmpty()) {
            drawLine(g2, rxData, rxColor, padding, chartWidth, chartHeight);
        }
        if (!txData.isEmpty()) {
            drawLine(g2, txData, txColor, padding, chartWidth, chartHeight);
        }
    }
    
    private void drawLine(Graphics2D g2, List<Double> data, Color color, 
                          int padding, int chartWidth, int chartHeight) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        
        int[] xPoints = new int[data.size()];
        int[] yPoints = new int[data.size()];
        
        for (int i = 0; i < data.size(); i++) {
            xPoints[i] = padding + (i * chartWidth) / Math.max(1, MAX_POINTS - 1);
            double ratio = data.get(i) / maxSpeed;
            yPoints[i] = padding + chartHeight - (int) (ratio * chartHeight);
        }
        
        for (int i = 0; i < data.size() - 1; i++) {
            g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }
    }
}
