package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 * 标签页拖拽辅助类
 * 支持标签页的拖放操作
 */
public class TabDragHelper implements DragGestureListener, DragSourceListener {
    
    private Component draggedComponent;
    private int draggedIndex = -1;
    private DragSource dragSource;
    private JTabbedPane tabbedPane;
    private Point dragPoint;
    private boolean isDragging = false;
    
    public TabDragHelper(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
        this.dragSource = new DragSource();
        dragSource.createDefaultDragGestureRecognizer(
            tabbedPane, DnDConstants.ACTION_MOVE, this);
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        Point p = dge.getDragOrigin();
        draggedIndex = tabbedPane.indexAtLocation(p.x, p.y);
        if (draggedIndex < 0) return;
        
        draggedComponent = tabbedPane.getComponentAt(draggedIndex);
        String title = tabbedPane.getTitleAt(draggedIndex);
        
        Transferable transferable = new StringSelection(title);
        dragSource.startDrag(dge, Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR),
            transferable, this);
        isDragging = true;
    }
    
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {}
    
    @Override
    public void dragOver(DragSourceDragEvent dsde) {
        dragPoint = dsde.getLocation();
        SwingUtilities.convertPointFromScreen(dragPoint, tabbedPane);
    }
    
    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
    @Override
    public void dragExit(DragSourceEvent dse) {}
    
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        if (isDragging && dragPoint != null) {
            int targetIndex = tabbedPane.indexAtLocation(dragPoint.x, dragPoint.y);
            if (targetIndex >= 0 && targetIndex != draggedIndex) {
                moveTab(draggedIndex, targetIndex);
            }
        }
        isDragging = false;
        draggedIndex = -1;
        draggedComponent = null;
        dragPoint = null;
    }
    
    private void moveTab(int fromIndex, int toIndex) {
        if (fromIndex < 0 || toIndex < 0) return;
        if (fromIndex == toIndex) return;
        
        Component comp = tabbedPane.getComponentAt(fromIndex);
        String title = tabbedPane.getTitleAt(fromIndex);
        Icon icon = tabbedPane.getIconAt(fromIndex);
        String tip = tabbedPane.getToolTipTextAt(fromIndex);
        
        tabbedPane.removeTabAt(fromIndex);
        tabbedPane.insertTab(title, icon, comp, tip, toIndex);
        tabbedPane.setSelectedIndex(toIndex);
    }
    
    public boolean isDragging() { return isDragging; }
    public int getDraggedIndex() { return draggedIndex; }
    public Component getDraggedComponent() { return draggedComponent; }
}
