package com.finalshell.ui;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.config.FolderConfig;
import com.finalshell.terminal.TerminalPanel;
import com.finalshell.ui.SessionTabPanel;
import com.finalshell.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Connection Tree Panel - Left side panel showing connections
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: OpenPanel_DeepAnalysis.md, UI_Parameters_Reference.md
 */
public class ConnectTreePanel extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectTreePanel.class);
    
    private final MainWindow mainWindow;
    private final ConfigManager configManager;
    
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTextField searchField;
    
    public ConnectTreePanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.configManager = ConfigManager.getInstance();
        
        initComponents();
        initLayout();
        loadConnections();
    }
    
    private void initComponents() {
        // Search field
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "搜索连接...");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTree(searchField.getText());
            }
        });
        
        // Tree
        rootNode = new DefaultMutableTreeNode("连接");
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new ConnectionTreeCellRenderer());
        
        // Double click to connect
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopupMenu(e);
                }
            }
        });
    }
    
    private void initLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Search panel at top
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }
    
    private void loadConnections() {
        rootNode.removeAllChildren();
        
        // Load folders first
        Map<String, DefaultMutableTreeNode> folderNodes = new HashMap<>();
        for (FolderConfig folder : configManager.getFolders()) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder);
            folderNodes.put(folder.getId(), folderNode);
            
            String parentId = folder.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootNode.add(folderNode);
            } else {
                DefaultMutableTreeNode parent = folderNodes.get(parentId);
                if (parent != null) {
                    parent.add(folderNode);
                } else {
                    rootNode.add(folderNode);
                }
            }
        }
        
        // Load connections
        for (ConnectConfig config : configManager.getConnections().values()) {
            DefaultMutableTreeNode connNode = new DefaultMutableTreeNode(config);
            
            String parentId = config.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                rootNode.add(connNode);
            } else {
                DefaultMutableTreeNode parent = folderNodes.get(parentId);
                if (parent != null) {
                    parent.add(connNode);
                } else {
                    rootNode.add(connNode);
                }
            }
        }
        
        treeModel.reload();
        expandAll();
        
        logger.info("Loaded {} connections", configManager.getConnections().size());
    }
    
    private void handleDoubleClick() {
        TreePath path = tree.getSelectionPath();
        if (path == null) return;
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = node.getUserObject();
        
        if (userObject instanceof ConnectConfig) {
            ConnectConfig config = (ConnectConfig) userObject;
            openConnection(config);
        }
    }
    
    private void openConnection(ConnectConfig config) {
        logger.info("Opening connection: {}", config.getName());
        mainWindow.setStatus("正在连接: " + config.getName());
        
        // Create session tab panel with terminal + SFTP support
        SessionTabPanel sessionPanel = new SessionTabPanel(config);
        
        Icon icon = ResourceLoader.getInstance().getIcon("images/terminal.png", 16, 16);
        mainWindow.addTab(config.getName(), icon, sessionPanel);
        
        // Start connection in background
        sessionPanel.connect();
    }
    
    private void showPopupMenu(MouseEvent e) {
        TreePath path = tree.getPathForLocation(e.getX(), e.getY());
        if (path != null) {
            tree.setSelectionPath(path);
        }
        
        JPopupMenu popup = new JPopupMenu();
        
        JMenuItem newConnItem = new JMenuItem("新建连接");
        newConnItem.addActionListener(ev -> mainWindow.showNewConnectionDialog());
        popup.add(newConnItem);
        
        JMenuItem newFolderItem = new JMenuItem("新建文件夹");
        newFolderItem.addActionListener(ev -> createNewFolder());
        popup.add(newFolderItem);
        
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();
            
            popup.addSeparator();
            
            if (userObject instanceof ConnectConfig) {
                ConnectConfig config = (ConnectConfig) userObject;
                
                JMenuItem connectItem = new JMenuItem("连接");
                connectItem.addActionListener(ev -> openConnection(config));
                popup.add(connectItem);
                
                JMenuItem editItem = new JMenuItem("编辑");
                editItem.addActionListener(ev -> editConnection(config));
                popup.add(editItem);
                
                JMenuItem deleteItem = new JMenuItem("删除");
                deleteItem.addActionListener(ev -> deleteConnection(config));
                popup.add(deleteItem);
            } else if (userObject instanceof FolderConfig) {
                FolderConfig folder = (FolderConfig) userObject;
                
                JMenuItem renameItem = new JMenuItem("重命名");
                renameItem.addActionListener(ev -> renameFolder(folder));
                popup.add(renameItem);
                
                JMenuItem deleteItem = new JMenuItem("删除");
                deleteItem.addActionListener(ev -> deleteFolder(folder));
                popup.add(deleteItem);
            }
        }
        
        popup.show(tree, e.getX(), e.getY());
    }
    
    public void createNewFolder() {
        String name = JOptionPane.showInputDialog(this, "文件夹名称:", "新建文件夹", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            FolderConfig folder = new FolderConfig(name.trim());
            configManager.addFolder(folder);
            loadConnections();
        }
    }
    
    private void editConnection(ConnectConfig config) {
        ConnectionDialog dialog = new ConnectionDialog(mainWindow, config);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadConnections();
        }
    }
    
    private void deleteConnection(ConnectConfig config) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除连接 \"" + config.getName() + "\" 吗？",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            configManager.deleteConnection(config.getId());
            loadConnections();
        }
    }
    
    private void renameFolder(FolderConfig folder) {
        String name = JOptionPane.showInputDialog(this, "文件夹名称:", folder.getName());
        if (name != null && !name.trim().isEmpty()) {
            folder.setName(name.trim());
            configManager.saveFolders();
            loadConnections();
        }
    }
    
    private void deleteFolder(FolderConfig folder) {
        int result = JOptionPane.showConfirmDialog(
            this,
            "确定要删除文件夹 \"" + folder.getName() + "\" 吗？\n文件夹内的连接不会被删除。",
            "确认删除",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            configManager.removeFolder(folder.getId());
            loadConnections();
        }
    }
    
    private void filterTree(String filter) {
        if (filter == null || filter.isEmpty()) {
            loadConnections();
            return;
        }
        
        String lowerFilter = filter.toLowerCase();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        filterNode(root, lowerFilter);
        treeModel.reload();
        expandAll();
    }
    
    private boolean filterNode(DefaultMutableTreeNode node, String filter) {
        boolean hasMatch = false;
        
        for (int i = node.getChildCount() - 1; i >= 0; i--) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            Object userObject = child.getUserObject();
            
            String name = "";
            if (userObject instanceof com.finalshell.config.ConnectConfig) {
                name = ((com.finalshell.config.ConnectConfig) userObject).getName();
            } else if (userObject instanceof com.finalshell.config.FolderConfig) {
                name = ((com.finalshell.config.FolderConfig) userObject).getName();
            } else if (userObject instanceof String) {
                name = (String) userObject;
            }
            
            boolean childMatch = filterNode(child, filter);
            boolean selfMatch = name.toLowerCase().contains(filter);
            
            if (!childMatch && !selfMatch) {
                node.remove(child);
            } else {
                hasMatch = true;
            }
        }
        
        return hasMatch;
    }
    
    public void collapseAll() {
        for (int i = tree.getRowCount() - 1; i >= 0; i--) {
            tree.collapseRow(i);
        }
    }
    
    public void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    public void refresh() {
        loadConnections();
    }
    
    public void refreshTree() {
        refresh();
    }
    
    /**
     * Custom cell renderer for connection tree
     */
    private class ConnectionTreeCellRenderer extends DefaultTreeCellRenderer {
        
        private final Icon folderIcon;
        private final Icon folderOpenIcon;
        private final Icon connectionIcon;
        
        public ConnectionTreeCellRenderer() {
            ResourceLoader loader = ResourceLoader.getInstance();
            folderIcon = loader.getIcon("images/folder.png", 16, 16);
            folderOpenIcon = loader.getIcon("images/folder-open.png", 16, 16);
            connectionIcon = loader.getIcon("images/screen.png", 16, 16);
        }
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            
            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
            
            if (value instanceof DefaultMutableTreeNode) {
                Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
                
                if (userObject instanceof FolderConfig) {
                    FolderConfig folder = (FolderConfig) userObject;
                    setText(folder.getName());
                    setIcon(expanded ? folderOpenIcon : folderIcon);
                } else if (userObject instanceof ConnectConfig) {
                    ConnectConfig config = (ConnectConfig) userObject;
                    setText(config.getName());
                    setIcon(connectionIcon);
                }
            }
            
            return this;
        }
    }
}
