package com.finalshell.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * 自定义滚动条UI
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MyScrollBarUI extends BasicScrollBarUI {
    
    private Color thumbColor = new Color(180, 180, 180);
    private Color trackColor = new Color(240, 240, 240);
    private int thumbWidth = 8;
    
    @Override
    protected void configureScrollBarColors() {
        this.thumbHighlightColor = thumbColor.brighter();
        this.thumbLightShadowColor = thumbColor;
        this.thumbDarkShadowColor = thumbColor.darker();
        this.thumbColor = this.thumbColor;
        this.trackColor = this.trackColor;
        this.trackHighlightColor = trackColor;
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }
    
    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(thumbColor);
        
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            int x = thumbBounds.x + (thumbBounds.width - thumbWidth) / 2;
            g2.fillRoundRect(x, thumbBounds.y, thumbWidth, thumbBounds.height, thumbWidth, thumbWidth);
        } else {
            int y = thumbBounds.y + (thumbBounds.height - thumbWidth) / 2;
            g2.fillRoundRect(thumbBounds.x, y, thumbBounds.width, thumbWidth, thumbWidth, thumbWidth);
        }
        
        g2.dispose();
    }
    
    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(trackColor);
        g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
    }
    
    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(thumbWidth, 30);
    }
    
    public void setThumbColor(Color color) {
        this.thumbColor = color;
    }
    
    public void setTrackColor(Color color) {
        this.trackColor = color;
    }
    
    public void setThumbWidth(int width) {
        this.thumbWidth = width;
    }
}
