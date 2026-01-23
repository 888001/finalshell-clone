package com.finalshell.ui.layout;

import java.awt.*;

/**
 * 覆盖布局管理器
 * 将所有子组件堆叠在同一位置
 */
public class OverLayoutManager implements LayoutManager {
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }
    
    @Override
    public void removeLayoutComponent(Component comp) {
    }
    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int maxWidth = 0;
            int maxHeight = 0;
            
            for (int i = 0; i < parent.getComponentCount(); i++) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    maxWidth = Math.max(maxWidth, d.width);
                    maxHeight = Math.max(maxHeight, d.height);
                }
            }
            
            Insets insets = parent.getInsets();
            return new Dimension(maxWidth + insets.left + insets.right,
                                 maxHeight + insets.top + insets.bottom);
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        synchronized (parent.getTreeLock()) {
            int maxWidth = 0;
            int maxHeight = 0;
            
            for (int i = 0; i < parent.getComponentCount(); i++) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    Dimension d = c.getMinimumSize();
                    maxWidth = Math.max(maxWidth, d.width);
                    maxHeight = Math.max(maxHeight, d.height);
                }
            }
            
            Insets insets = parent.getInsets();
            return new Dimension(maxWidth + insets.left + insets.right,
                                 maxHeight + insets.top + insets.bottom);
        }
    }
    
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth() - insets.left - insets.right;
            int height = parent.getHeight() - insets.top - insets.bottom;
            
            for (int i = 0; i < parent.getComponentCount(); i++) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    c.setBounds(insets.left, insets.top, width, height);
                }
            }
        }
    }
}
