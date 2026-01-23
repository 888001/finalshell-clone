package com.finalshell.sftp;

import javax.swing.*;
import java.awt.*;

/**
 * 传输进度条
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TransProgressBar extends JProgressBar {
    
    private Color progressColor = new Color(76, 175, 80);
    private Color pausedColor = new Color(255, 193, 7);
    private Color errorColor = new Color(244, 67, 54);
    private boolean paused;
    private boolean error;
    
    public TransProgressBar() {
        super(0, 100);
        setStringPainted(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);
        
        int progress = (int) ((getValue() / (double) getMaximum()) * width);
        
        if (error) {
            g2.setColor(errorColor);
        } else if (paused) {
            g2.setColor(pausedColor);
        } else {
            g2.setColor(progressColor);
        }
        g2.fillRect(0, 0, progress, height);
        
        g2.setColor(getForeground());
        String text = getString();
        FontMetrics fm = g2.getFontMetrics();
        int x = (width - fm.stringWidth(text)) / 2;
        int y = (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, x, y);
        
        g2.dispose();
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
        repaint();
    }
    
    public void setError(boolean error) {
        this.error = error;
        repaint();
    }
    
    public void setProgressColor(Color color) {
        this.progressColor = color;
        repaint();
    }
}
