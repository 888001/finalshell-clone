package com.finalshell.ui.filetree;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.config.FolderConfig;
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
        setEditable(true);
        
        rootNode = (DefaultMutableTreeNode) getModel().getRoot();
        treeModel = (DefaultTreeModel) getModel();
        
        connRootNode = new DefaultMutableTreeNode("连接", true);
        rootNode.add(connRootNode);
        
        cellRenderer = new FileTreeCellRenderer();
        setCellRenderer(cellRenderer);
        
        setDragEnabled(true);
        setDropMode(DropMode.ON_OR_INSERT);
        setTransferHandler(new TreeTransferHandler(this));

        reloadFromConfigManager();
    }
    
    private void initListeners() {
        addTreeExpansionListener(new TreeExpansionListener() {
            @Override
            public void treeExpanded(TreeExpansionEvent event) {
                Object obj = ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject();
                if (obj instanceof VDir) {
                    FolderConfig folder = ConfigManager.getInstance().getFolderById(((VDir) obj).getId());
                    if (folder != null && !folder.isExpanded()) {
                        folder.setExpanded(true);
                        ConfigManager.getInstance().updateFolder(folder);
                    }
                }
            }

            @Override
            public void treeCollapsed(TreeExpansionEvent event) {
                Object obj = ((DefaultMutableTreeNode) event.getPath().getLastPathComponent()).getUserObject();
                if (obj instanceof VDir) {
                    FolderConfig folder = ConfigManager.getInstance().getFolderById(((VDir) obj).getId());
                    if (folder != null && folder.isExpanded()) {
                        folder.setExpanded(false);
                        ConfigManager.getInstance().updateFolder(folder);
                    }
                }
            }
        });

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
        reloadFromConfigManager();
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

    public void reloadFromConfigManager() {
        connRootNode.removeAllChildren();

        Map<String, DefaultMutableTreeNode> folderNodes = new HashMap<>();
        java.util.List<FolderConfig> folders = ConfigManager.getInstance().getFolders();

        for (FolderConfig folder : folders) {
            VDir dir = new VDir(folder.getName());
            dir.setId(folder.getId());
            dir.setCreateTime(new java.sql.Timestamp(folder.getUpdateTime()));
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(dir, true);
            folderNodes.put(folder.getId(), folderNode);
        }

        for (FolderConfig folder : folders) {
            DefaultMutableTreeNode folderNode = folderNodes.get(folder.getId());
            String parentId = folder.getParentId();
            if (parentId == null || parentId.isEmpty() || "root".equals(parentId)) {
                connRootNode.add(folderNode);
            } else {
                DefaultMutableTreeNode parent = folderNodes.get(parentId);
                if (parent != null) {
                    parent.add(folderNode);
                } else {
                    connRootNode.add(folderNode);
                }
            }
        }

        for (ConnectConfig config : ConfigManager.getInstance().getConnections().values()) {
            VFile vfile = new VFile();
            vfile.setId(config.getId());
            vfile.setName(config.getName());
            vfile.setType(VFile.TYPE_FILE);
            vfile.setCreateTime(new java.sql.Timestamp(config.getUpdateTime()));
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(vfile, false);

            String folderId = config.getParentId();
            if (folderId == null || folderId.isEmpty() || "root".equals(folderId)) {
                connRootNode.add(node);
            } else {
                DefaultMutableTreeNode parent = folderNodes.get(folderId);
                if (parent != null) {
                    parent.add(node);
                } else {
                    connRootNode.add(node);
                }
            }
        }

        treeModel.reload(connRootNode);
        expandPath(new TreePath(connRootNode.getPath()));

        javax.swing.SwingUtilities.invokeLater(() -> applyFolderExpandedStates(connRootNode));
    }

    private void applyFolderExpandedStates(DefaultMutableTreeNode node) {
        if (node == null) return;

        Object obj = node.getUserObject();
        if (obj instanceof VDir) {
            FolderConfig folder = ConfigManager.getInstance().getFolderById(((VDir) obj).getId());
            if (folder != null) {
                TreePath path = new TreePath(node.getPath());
                if (folder.isExpanded()) {
                    expandPath(path);
                } else {
                    collapsePath(path);
                }
            }
        }

        java.util.Enumeration children = node.children();
        while (children.hasMoreElements()) {
            Object c = children.nextElement();
            if (c instanceof DefaultMutableTreeNode) {
                applyFolderExpandedStates((DefaultMutableTreeNode) c);
            }
        }
    }

    /**
     * Handle tree node value changes (for inline editing)
     */
    public void handleNodeValueChange(TreePath path, Object newValue) {
        if (path == null || !(path.getLastPathComponent() instanceof DefaultMutableTreeNode)) {
            return;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = node.getUserObject();
        String text = newValue != null ? newValue.toString().trim() : "";
        if (text.isEmpty()) {
            return;
        }

        if (userObject instanceof VDir) {
            VDir dir = (VDir) userObject;
            dir.setName(text);
            node.setUserObject(dir);
            treeModel.nodeChanged(node);

            FolderConfig folder = ConfigManager.getInstance().getFolderById(dir.getId());
            if (folder != null) {
                folder.setName(text);
                ConfigManager.getInstance().updateFolder(folder);
            }
            return;
        }

        if (userObject instanceof VFile) {
            VFile vfile = (VFile) userObject;
            vfile.setName(text);
            node.setUserObject(vfile);
            treeModel.nodeChanged(node);

            ConnectConfig config = ConfigManager.getInstance().getConnectionById(vfile.getId());
            if (config != null) {
                config.setName(text);
                ConfigManager.getInstance().saveConnection(config);
            }
        }
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
