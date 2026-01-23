package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;

/**
 * 自定义标签组件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - TLabel
 */
public class TLabel extends JLabel {
    
    private Color hoverColor;
    private Color normalColor;
    private boolean underline = false;
    private boolean hover = false;
    
    public TLabel() {
        super();
        init();
    }
    
    public TLabel(String text) {
        super(text);
        init();
    }
    
    public TLabel(String text, Icon icon) {
        super(text);
        setIcon(icon);
        init();
    }
    
    private void init() {
        normalColor = getForeground();
        hoverColor = new Color(0x0066CC);
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hover = true;
                setForeground(hoverColor);
                if (underline) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
                repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hover = false;
                setForeground(normalColor);
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (underline && hover) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int y = fm.getAscent() + 1;
            g2.drawLine(0, y, fm.stringWidth(getText()), y);
        }
    }
    
    public void setUnderline(boolean underline) {
        this.underline = underline;
    }
    
    public boolean isUnderline() {
        return underline;
    }
    
    public void setHoverColor(Color color) {
        this.hoverColor = color;
    }
    
    public Color getHoverColor() {
        return hoverColor;
    }
    
    public void setNormalColor(Color color) {
        this.normalColor = color;
        if (!hover) {
            setForeground(color);
        }
    }
}
