package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 拖拽窗口
 * 用于标签页拖拽时显示的预览窗口
 */
public class DragWin extends JWindow {
    
    private Component content;
    private String title;
    private float alpha = 0.7f;
    
    public DragWin(Window owner) {
        super(owner);
        setAlwaysOnTop(true);
        initUI();
    }
    
    private void initUI() {
        setBackground(new Color(0, 0, 0, 0));
        setSize(200, 150);
    }
    
    public void setContent(Component content) {
        this.content = content;
        getContentPane().removeAll();
        if (content != null) {
            getContentPane().add(content, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void showAt(Point location) {
        setLocation(location);
        setVisible(true);
    }
    
    public void hide() {
        setVisible(false);
    }
    
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paint(g2);
        g2.dispose();
    }
    
    public void setAlpha(float alpha) {
        this.alpha = alpha;
        repaint();
    }
}
