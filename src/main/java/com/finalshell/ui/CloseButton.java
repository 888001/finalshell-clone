package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 关闭按钮
 * 用于标签页的关闭按钮
 */
public class CloseButton extends JButton {
    
    private static final int SIZE = 16;
    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private Color normalColor = new Color(150, 150, 150);
    private Color hoverColor = new Color(200, 80, 80);
    private Color pressedColor = new Color(180, 60, 60);
    
    public CloseButton() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setRolloverEnabled(true);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOver = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                mouseOver = false;
                repaint();
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (mouseOver) {
            g2.setColor(new Color(255, 200, 200, 100));
            g2.fillOval(0, 0, SIZE - 1, SIZE - 1);
        }
        
        Color xColor = mousePressed ? pressedColor : (mouseOver ? hoverColor : normalColor);
        g2.setColor(xColor);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int margin = 4;
        g2.drawLine(margin, margin, SIZE - margin - 1, SIZE - margin - 1);
        g2.drawLine(SIZE - margin - 1, margin, margin, SIZE - margin - 1);
        
        g2.dispose();
    }
    
    public void setNormalColor(Color color) { this.normalColor = color; }
    public void setHoverColor(Color color) { this.hoverColor = color; }
    public void setPressedColor(Color color) { this.pressedColor = color; }
}
