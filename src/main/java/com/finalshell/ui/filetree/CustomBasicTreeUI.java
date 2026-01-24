package com.finalshell.ui.filetree;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 自定义树形UI
 * 扩展BasicTreeUI提供自定义外观和行为
 */
public class CustomBasicTreeUI extends BasicTreeUI {
    
    private Color selectionBackground;
    private Color selectionForeground;
    private Color hoverBackground;
    private int hoveredRow = -1;
    
    public CustomBasicTreeUI() {
        super();
    }
    
    @Override
    protected void installDefaults() {
        super.installDefaults();
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        hoverBackground = new Color(230, 240, 250);
    }
    
    @Override
    protected void installListeners() {
        super.installListeners();
        
        tree.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tree.getRowForLocation(e.getX(), e.getY());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    tree.repaint();
                }
            }
        });
        
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                tree.repaint();
            }
        });
    }
    
    @Override
    protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets, 
            Rectangle bounds, TreePath path, int row, boolean isExpanded, 
            boolean hasBeenExpanded, boolean isLeaf) {
        
        if (tree.isRowSelected(row)) {
            g.setColor(selectionBackground);
            g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
        } else if (row == hoveredRow) {
            g.setColor(hoverBackground);
            g.fillRect(0, bounds.y, tree.getWidth(), bounds.height);
        }
        
        super.paintRow(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);
    }
    
    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        // 可选：自定义水平线绘制
        super.paintHorizontalLine(g, c, y, left, right);
    }
    
    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
        // 可选：自定义垂直线绘制
        super.paintVerticalLine(g, c, x, top, bottom);
    }
    
    public void setSelectionBackground(Color color) {
        this.selectionBackground = color;
    }
    
    public void setSelectionForeground(Color color) {
        this.selectionForeground = color;
    }
    
    public void setHoverBackground(Color color) {
        this.hoverBackground = color;
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new CustomBasicTreeUI();
    }
}
