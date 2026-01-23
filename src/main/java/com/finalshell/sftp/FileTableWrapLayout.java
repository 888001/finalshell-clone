package com.finalshell.sftp;

import java.awt.*;

/**
 * 文件表格包装布局
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FileTableWrapLayout implements LayoutManager {
    
    private int hgap;
    private int vgap;
    
    public FileTableWrapLayout() {
        this(5, 5);
    }
    
    public FileTableWrapLayout(int hgap, int vgap) {
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
            if (width == 0) width = 400;
            
            int x = insets.left + hgap;
            int y = insets.top + vgap;
            int rowHeight = 0;
            
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    if (x + d.width + hgap > width - insets.right) {
                        x = insets.left + hgap;
                        y += rowHeight + vgap;
                        rowHeight = 0;
                    }
                    x += d.width + hgap;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            y += rowHeight + vgap;
            
            return new Dimension(width, y + insets.bottom);
        }
    }
    
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(100, 100);
    }
    
    @Override
    public void layoutContainer(Container parent) {
        synchronized (parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int width = parent.getWidth();
            
            int x = insets.left + hgap;
            int y = insets.top + vgap;
            int rowHeight = 0;
            
            for (Component comp : parent.getComponents()) {
                if (comp.isVisible()) {
                    Dimension d = comp.getPreferredSize();
                    if (x + d.width + hgap > width - insets.right) {
                        x = insets.left + hgap;
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
