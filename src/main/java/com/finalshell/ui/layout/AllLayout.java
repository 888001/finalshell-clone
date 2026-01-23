package com.finalshell.ui.layout;

import java.awt.*;

/**
 * 全填充布局
 * 使所有子组件填满整个容器
 */
public class AllLayout implements LayoutManager {
    
    @Override
    public void addLayoutComponent(String name, Component comp) {}
    
    @Override
    public void removeLayoutComponent(Component comp) {}
    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int w = 0, h = 0;
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    w = Math.max(w, d.width);
                    h = Math.max(h, d.height);
                }
            }
            return new Dimension(w + insets.left + insets.right, 
                                h + insets.top + insets.bottom);
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int w = 0, h = 0;
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getMinimumSize();
                    w = Math.max(w, d.width);
                    h = Math.max(h, d.height);
                }
            }
            return new Dimension(w + insets.left + insets.right, 
                                h + insets.top + insets.bottom);
        }
    }
    
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int x = insets.left;
            int y = insets.top;
            int w = parent.getWidth() - insets.left - insets.right;
            int h = parent.getHeight() - insets.top - insets.bottom;
            
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    comp.setBounds(x, y, w, h);
                }
            }
        }
    }
}
