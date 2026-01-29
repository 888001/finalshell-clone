package com.finalshell.ui.filetree;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.*;
import com.finalshell.util.FileSortConfig;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

/**
 * 连接管理文件树组件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - FileTree
 */
public class FileTree extends JTree {
    
    private static final long serialVersionUID = -1929830465908409567L;
    
    private FileTreeCellRenderer cellRenderer;
    private DefaultTreeModel treeModel;
    private HashMap<String, DefaultMutableTreeNode> nodePathMap = new HashMap<>();
    
    private VDir rootDir;
    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode connRootNode;
    
    private long lastSelectTime;
    private boolean isMousePressed;
    private TreePath pressedPath;
    private int editDelay = 300;
    
    private FileSortConfig fileSortConfig;
    private OpenPanel openPanel;
    private FileTreePopupMenu popupMenu;
    private boolean isSearchMode;
    private String currentDirPath;
    private HashSet<VDir> expandedDirs = new HashSet<>();
    
    public FileTree(OpenPanel openPanel, boolean forSearch) {
        super(new DefaultMutableTreeNode("treeroot", true));
        this.isSearchMode = forSearch;
        this.openPanel = openPanel;
        
        initTree();
        initListeners();
    }
    
    private void initTree() {
        setBorder(null);
        setOpaque(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        setRootVisible(false);
        setShowsRootHandles(true);
        
        rootNode = (DefaultMutableTreeNode) getModel().getRoot();
        treeModel = (DefaultTreeModel) getModel();
        
        connRootNode = new DefaultMutableTreeNode("连接", true);
        rootNode.add(connRootNode);
        
        cellRenderer = new FileTreeCellRenderer();
        setCellRenderer(cellRenderer);
        
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
    }
    
    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                isMousePressed = true;
                pressedPath = getPathForLocation(e.getX(), e.getY());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isMousePressed = false;
                TreePath path = getPathForLocation(e.getX(), e.getY());
                
                if (path == null) return;
                
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                
                // 右键菜单
                if (e.getButton() == MouseEvent.BUTTON3) {
                    showPopupMenu(e, node);
                    return;
                }
                
                // 双击打开连接
                if (e.getClickCount() == 2 && userObject instanceof VFile) {
                    VFile file = (VFile) userObject;
                    openConnection(file);
                }
            }
        });
        
        addTreeSelectionListener(e -> {
            lastSelectTime = System.currentTimeMillis();
        });
    }
    
    private void showPopupMenu(MouseEvent e, DefaultMutableTreeNode node) {
        boolean isRoot = (node == connRootNode);
        popupMenu = new FileTreePopupMenu(this, node, isRoot, isSearchMode);
        popupMenu.show(e.getComponent(), e.getX() + 1, e.getY() + 1);
    }
    
    private void openConnection(VFile file) {
        if (openPanel == null || file == null) return;
        
        ConnectConfig config = ConfigManager.getInstance().getConnectionById(file.getId());
        if (config != null) {
            openPanel.openConnection(config);
        }
    }
    
    public void addConnection(ConnectConfig config) {
        VFile vfile = new VFile();
        vfile.setName(config.getName());
        vfile.setId(config.getId());
        vfile.setCreateTime(new java.sql.Timestamp(config.getUpdateTime()));
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(vfile);
        connRootNode.add(node);
        treeModel.reload(connRootNode);
    }
    
    public void addFolder(VDir dir) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);
        connRootNode.add(node);
        treeModel.reload(connRootNode);
    }
    
    public void refresh() {
        treeModel.reload();
        expandPath(new TreePath(connRootNode.getPath()));
    }
    
    public void clear() {
        connRootNode.removeAllChildren();
        treeModel.reload(connRootNode);
    }
    
    public DefaultMutableTreeNode getConnRootNode() {
        return connRootNode;
    }
    
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }
    
    public OpenPanel getOpenPanel() {
        return openPanel;
    }
    
    public boolean isSearchMode() {
        return isSearchMode;
    }
    
    // Alias method for compatibility
    public void refreshTree() {
        refresh();
    }
    
    public void refreshNode(DefaultMutableTreeNode node) {
        treeModel.reload(node);
    }
    
    public void fireConnectEvent(VFile vfile) {
        if (openPanel != null && vfile != null) {
            openConnection(vfile);
        }
    }
    
    public void sortChildren(DefaultMutableTreeNode node, java.util.Comparator<Object> comparator) {
        if (node == null) return;
        java.util.List<DefaultMutableTreeNode> children = new java.util.ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            children.add((DefaultMutableTreeNode) node.getChildAt(i));
        }
        children.sort((n1, n2) -> comparator.compare(n1.getUserObject(), n2.getUserObject()));
        node.removeAllChildren();
        for (DefaultMutableTreeNode child : children) {
            node.add(child);
        }
        treeModel.reload(node);
    }
}
