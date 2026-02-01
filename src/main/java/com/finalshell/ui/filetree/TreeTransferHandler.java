package com.finalshell.ui.filetree;

import com.finalshell.config.ConfigManager;
import com.finalshell.ui.VDir;
import com.finalshell.ui.VFile;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 树形节点拖拽处理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - TreeTransferHandler
 */
public class TreeTransferHandler extends TransferHandler {
    
    private DataFlavor nodesFlavor;
    private DataFlavor[] flavors = new DataFlavor[1];
    private final FileTree fileTree;
    
    public TreeTransferHandler(FileTree fileTree) {
        this.fileTree = fileTree;
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType +
                    ";class=\"" + DefaultMutableTreeNode[].class.getName() + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
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
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }

        // 检查拖拽动作
        if ((support.getDropAction() & MOVE) == 0) {
            return false;
        }
        
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        if (dl == null || dl.getPath() == null) {
            return false;
        }
        JTree tree = (JTree) support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        if (selRows != null) {
            for (int selRow : selRows) {
                if (selRow == dropRow) {
                    return false;
                }
            }
        }
        
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest.getLastPathComponent();
        Object targetObject = target.getUserObject();
        if (!(targetObject instanceof VDir) && target != fileTree.getConnRootNode()) {
            return false;
        }

        TreePath[] selPaths = tree.getSelectionPaths();
        if (selPaths != null) {
            for (TreePath p : selPaths) {
                Object comp = p.getLastPathComponent();
                if (!(comp instanceof DefaultMutableTreeNode)) continue;
                DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) comp;

                Object selObject = selNode.getUserObject();
                if (!(selObject instanceof VDir) && !(selObject instanceof VFile)) {
                    return false;
                }

                if (selNode == target) {
                    return false;
                }
                if (selNode.isNodeAncestor(target)) {
                    return false;
                }
                if (selNode.getChildCount() > 0 && target.getLevel() < selNode.getLevel()) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            List<DefaultMutableTreeNode> raw = new ArrayList<>();
            Set<DefaultMutableTreeNode> set = new HashSet<>();
            for (TreePath p : paths) {
                Object cpt = p.getLastPathComponent();
                if (!(cpt instanceof DefaultMutableTreeNode)) continue;
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) cpt;
                Object uo = n.getUserObject();
                if (!(uo instanceof VDir) && !(uo instanceof VFile)) continue;
                raw.add(n);
                set.add(n);
            }

            List<DefaultMutableTreeNode> selected = new ArrayList<>();
            for (DefaultMutableTreeNode n : raw) {
                boolean hasAncestorSelected = false;
                TreeNode parent = n.getParent();
                while (parent instanceof DefaultMutableTreeNode) {
                    if (set.contains(parent)) {
                        hasAncestorSelected = true;
                        break;
                    }
                    parent = parent.getParent();
                }
                if (!hasAncestorSelected) {
                    selected.add(n);
                }
            }

            if (selected.isEmpty()) {
                return null;
            }

            DefaultMutableTreeNode[] nodes = selected.toArray(new DefaultMutableTreeNode[0]);
            return new NodesTransferable(nodes);
        }
        return null;
    }
    
    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
        
    }
    
    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }
    
    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nodes == null || nodes.length == 0) {
            return false;
        }
        
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        if (dl == null || dl.getPath() == null) {
            return false;
        }
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        
        int index = childIndex;
        if (childIndex == -1) {
            index = parent.getChildCount();
        }

        for (DefaultMutableTreeNode node : nodes) {
            MutableTreeNode oldParent = (MutableTreeNode) node.getParent();
            int oldIndex = -1;
            if (oldParent != null) {
                oldIndex = ((DefaultMutableTreeNode) oldParent).getIndex(node);
                model.removeNodeFromParent(node);
            }

            if (oldParent == parent && oldIndex >= 0 && oldIndex < index) {
                index--;
            }

            model.insertNodeInto(node, parent, index++);
        }

        String newParentId = "root";
        Object parentObject = parent.getUserObject();
        if (parentObject instanceof VDir) {
            newParentId = ((VDir) parentObject).getId();
        }

        for (DefaultMutableTreeNode node : nodes) {
            Object obj = node.getUserObject();
            if (obj instanceof VFile) {
                ConfigManager.getInstance().moveConnection(((VFile) obj).getId(), newParentId);
            } else if (obj instanceof VDir) {
                ConfigManager.getInstance().moveFolder(((VDir) obj).getId(), newParentId);
            }
        }

        return true;
    }
    
    private class NodesTransferable implements Transferable {
        DefaultMutableTreeNode[] nodes;
        
        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }
        
        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return nodes;
        }
        
        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }
        
        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }
}
