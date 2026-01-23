package com.finalshell.ui;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 * 树形控件拖拽支持变体
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TreeDragAndDrop1 extends JTree implements DragGestureListener, DragSourceListener, DropTargetListener {
    
    private DragSource dragSource;
    private DropTarget dropTarget;
    private TreePath dragPath;
    
    public TreeDragAndDrop1() {
        super();
        init();
    }
    
    public TreeDragAndDrop1(TreeModel model) {
        super(model);
        init();
    }
    
    private void init() {
        dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
        dropTarget = new DropTarget(this, DnDConstants.ACTION_MOVE, this);
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        Point location = dge.getDragOrigin();
        dragPath = getPathForLocation(location.x, location.y);
        if (dragPath != null) {
            Object node = dragPath.getLastPathComponent();
            if (node != null) {
                Transferable transferable = new StringSelection(node.toString());
                dragSource.startDrag(dge, DragSource.DefaultMoveDrop, transferable, this);
            }
        }
    }
    
    @Override
    public void dragEnter(DragSourceDragEvent dsde) {}
    
    @Override
    public void dragOver(DragSourceDragEvent dsde) {}
    
    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {}
    
    @Override
    public void dragExit(DragSourceEvent dse) {}
    
    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        dragPath = null;
    }
    
    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        dtde.acceptDrag(DnDConstants.ACTION_MOVE);
    }
    
    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        Point location = dtde.getLocation();
        TreePath path = getPathForLocation(location.x, location.y);
        if (path != null) {
            setSelectionPath(path);
        }
    }
    
    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {}
    
    @Override
    public void dragExit(DropTargetEvent dte) {}
    
    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            Point location = dtde.getLocation();
            TreePath targetPath = getPathForLocation(location.x, location.y);
            
            if (targetPath != null && dragPath != null) {
                dtde.acceptDrop(DnDConstants.ACTION_MOVE);
                dtde.dropComplete(true);
            } else {
                dtde.rejectDrop();
            }
        } catch (Exception e) {
            dtde.rejectDrop();
        }
    }
}
