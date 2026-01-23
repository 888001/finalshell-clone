package com.finalshell.ui.layout;

import java.awt.*;
import java.util.*;

/**
 * 浮动包装器布局管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FloatWrapperLayout implements LayoutManager {
    
    private Map<Component, Rectangle> bounds;
    
    public FloatWrapperLayout() {
        this.bounds = new HashMap<>();
    }
    
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }
    
    @Override
    public void removeLayoutComponent(Component comp) {
        bounds.remove(comp);
    }
    
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return new Dimension(400, 300);
    }
    
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(100, 100);
    }
    
    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int width = parent.getWidth() - insets.left - insets.right;
        int height = parent.getHeight() - insets.top - insets.bottom;
        
        for (Component comp : parent.getComponents()) {
            if (comp.isVisible()) {
                Rectangle r = bounds.get(comp);
                if (r != null) {
                    comp.setBounds(r);
                } else {
                    Dimension pref = comp.getPreferredSize();
                    comp.setBounds(insets.left, insets.top, 
                        Math.min(pref.width, width), 
                        Math.min(pref.height, height));
                }
            }
        }
    }
    
    public void setBounds(Component comp, Rectangle rect) {
        bounds.put(comp, rect);
    }
}
