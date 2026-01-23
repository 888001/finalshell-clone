package com.finalshell.ui.layout;

import java.awt.*;

/**
 * 树形包装布局管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TreeWrapLayout implements LayoutManager {
    
    private int hgap;
    private int vgap;
    
    public TreeWrapLayout() {
        this(5, 5);
    }
    
    public TreeWrapLayout(int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }
    
    @Override
    public void removeLayoutComponent(Component comp) {
    }
    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth();
            if (width == 0) width = Integer.MAX_VALUE;
            
            int maxWidth = width - insets.left - insets.right;
            int x = 0, y = 0, rowHeight = 0;
            
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    if (x > 0 && x + d.width > maxWidth) {
                        x = 0;
                        y += rowHeight + vgap;
                        rowHeight = 0;
                    }
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            
            return new Dimension(
                insets.left + insets.right + maxWidth,
                insets.top + insets.bottom + y + rowHeight
            );
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(100, 50);
    }
    
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int maxWidth = parent.getWidth() - insets.left - insets.right;
            int x = insets.left, y = insets.top, rowHeight = 0;
            
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    if (x > insets.left && x + d.width > maxWidth + insets.left) {
                        x = insets.left;
                        y += rowHeight + vgap;
                        rowHeight = 0;
                    }
                    comp.setBounds(x, y, d.width, d.height);
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
        }
    }
}
