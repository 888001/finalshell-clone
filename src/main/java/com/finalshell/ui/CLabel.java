package com.finalshell.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

import com.finalshell.monitor.MonitorData;
import com.finalshell.network.TracertNode;

/**
 * 自定义标签组件
 * 支持绘制Ping响应、丢包率、延迟等可视化信息
 */
public class CLabel extends JLabel {
    
    private static final long serialVersionUID = -3969858674688830220L;
    
    private boolean showPingHistory = false;
    private boolean showDropRate = false;
    private boolean showLatency = false;
    private TracertNode tracertNode;
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (tracertNode == null) {
            return;
        }
        
        MonitorData monitorData = tracertNode.getMonitorData();
        if (monitorData == null) {
            return;
        }
        
        if (showPingHistory) {
            drawPingHistory(g2d, monitorData);
        } else if (showDropRate) {
            drawDropRate(g2d, monitorData);
        } else if (showLatency) {
            drawLatency(g2d, monitorData);
        }
    }
    
    /**
     * 绘制Ping历史记录
     */
    private void drawPingHistory(Graphics2D g2d, MonitorData monitorData) {
        List<Long> pingList = monitorData.getPingResponseTimes();
        if (pingList == null || pingList.isEmpty()) {
            return;
        }
        
        int n = 0;
        int index = pingList.size() - 1;
        int cellWidth = 3;
        int cellHeight = 3;
        int leftSpace = 0;
        
        while (index >= 0) {
            long rtt = pingList.get(index);
            int cellX = getWidth() - n * cellWidth - 2;
            
            Color bgColor;
            if (rtt == 0) {
                bgColor = new Color(250, 120, 100, 150); // 红色表示超时
            } else {
                bgColor = new Color(100, 200, 100, 150); // 绿色表示成功
            }
            
            g2d.setColor(bgColor);
            g2d.fillRect(cellX, getHeight() - cellHeight, cellWidth, cellHeight);
            
            if (cellX < leftSpace) {
                break;
            }
            
            n++;
            index--;
        }
    }
    
    /**
     * 绘制丢包率
     */
    private void drawDropRate(Graphics2D g2d, MonitorData monitorData) {
        int received = monitorData.getReceivedCount();
        int dropped = monitorData.getDroppedCount();
        
        if (received == 0) {
            return;
        }
        
        float dropRate = (float) dropped / (float) received;
        int gap = 3;
        int baseW = 2;
        int leftGap = 5;
        int dropWidth = (int) ((getWidth() - leftGap) * dropRate) + baseW;
        int startY = gap;
        
        Color bgColor;
        if (dropRate > 0.0f) {
            bgColor = new Color(255, 140, 120, 120); // 红色表示有丢包
        } else {
            bgColor = new Color(100, 200, 100, 255); // 绿色表示无丢包
        }
        
        g2d.setColor(bgColor);
        g2d.fillRect(getWidth() - dropWidth, startY, dropWidth, getHeight() - 2 * gap);
    }
    
    /**
     * 绘制延迟
     */
    private void drawLatency(Graphics2D g2d, MonitorData monitorData) {
        long avgLatency = monitorData.getAverageLatency();
        
        Color bgColor = new Color(140, 200, 255, 150);
        g2d.setColor(bgColor);
        
        int gap = 3;
        int baseW = 2;
        float unit = (float) getWidth() / 500.0f;
        int drawWidth = (int) (unit * avgLatency + baseW);
        
        g2d.fillRect(getWidth() - drawWidth, gap, drawWidth, getHeight() - 2 * gap);
    }
    
    public void setShowPingHistory(boolean show) {
        this.showPingHistory = show;
    }
    
    public void setShowDropRate(boolean show) {
        this.showDropRate = show;
    }
    
    public void setShowLatency(boolean show) {
        this.showLatency = show;
    }
    
    public void setTracertNode(TracertNode node) {
        this.tracertNode = node;
    }
    
    public TracertNode getTracertNode() {
        return tracertNode;
    }
}
