package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 加载面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Nav_Loading_UI_DeepAnalysis.md - LoadingPanel
 */
public class LoadingPanel extends JPanel {
    
    private JLabel iconLabel;
    private JLabel messageLabel;
    private Timer animationTimer;
    private int animationFrame = 0;
    
    public LoadingPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255, 255, 255, 240));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));
        
        // 加载图标
        iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setPreferredSize(new Dimension(48, 48));
        add(iconLabel);
        
        add(Box.createVerticalStrut(10));
        
        // 消息标签
        messageLabel = new JLabel("加载中...");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        add(messageLabel);
        
        // 动画定时器
        animationTimer = new Timer(100, e -> {
            animationFrame = (animationFrame + 1) % 8;
            repaint();
        });
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        animationTimer.start();
    }
    
    @Override
    public void removeNotify() {
        animationTimer.stop();
        super.removeNotify();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 绘制简单的加载动画
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int centerX = iconLabel.getX() + iconLabel.getWidth() / 2;
        int centerY = iconLabel.getY() + iconLabel.getHeight() / 2;
        int radius = 15;
        
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8 - Math.PI / 2;
            int x = (int) (centerX + Math.cos(angle) * radius);
            int y = (int) (centerY + Math.sin(angle) * radius);
            
            int alpha = 255 - ((i - animationFrame + 8) % 8) * 30;
            g2.setColor(new Color(100, 100, 100, Math.max(50, alpha)));
            g2.fillOval(x - 4, y - 4, 8, 8);
        }
        
        g2.dispose();
    }
    
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
    
    public String getMessage() {
        return messageLabel.getText();
    }
}
