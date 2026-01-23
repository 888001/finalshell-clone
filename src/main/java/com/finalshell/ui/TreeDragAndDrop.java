package com.finalshell.ui;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;

/**
 * 树形拖拽支持
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - TreeDragAndDrop
 */
public class TreeDragAndDrop extends TransferHandler {
    
    private JTree tree;
    private DataFlavor nodeFlavor;
    private DataFlavor[] flavors = new DataFlavor[1];
    
    public TreeDragAndDrop(JTree tree) {
        this.tree = tree;
        try {
            nodeFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + 
                ";class=\"" + DefaultMutableTreeNode.class.getName() + "\"");
            flavors[0] = nodeFlavor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean canImport(TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodeFlavor)) {
            return false;
        }
        
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        TreePath path = dl.getPath();
        if (path == null) {
            return false;
        }
        
        return true;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null && paths.length > 0) {
            DefaultMutableTreeNode node = 
                (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            return new NodeTransferable(node);
        }
        return null;
    }
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        if (action == MOVE) {
            // 移动完成后的处理
        }
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) 
                support.getTransferable().getTransferData(nodeFlavor);
            
            JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
            TreePath path = dl.getPath();
            int childIndex = dl.getChildIndex();
            
            DefaultMutableTreeNode parent = 
                (DefaultMutableTreeNode) path.getLastPathComponent();
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            
            if (childIndex == -1) {
                childIndex = parent.getChildCount();
            }
            
            model.insertNodeInto(node, parent, childIndex);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private class NodeTransferable implements Transferable {
        private DefaultMutableTreeNode node;
        
        public NodeTransferable(DefaultMutableTreeNode node) {
            this.node = node;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
        
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodeFlavor.equals(flavor);
        }
        
        @Override
        public Object getTransferData(DataFlavor flavor) 
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return node;
        }
    }
}
