package com.finalshell.ui.filetree;

import com.finalshell.config.ConnectConfig;
import com.finalshell.config.FolderConfig;

import javax.swing.tree.*;
import java.util.*;

/**
 * 文件树数据模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - FileTreeModel
 */
public class FileTreeModel extends DefaultTreeModel {
    
    private List<ConnectConfig> connections = new ArrayList<>();
    private List<FolderConfig> folders = new ArrayList<>();
    
    public FileTreeModel() {
        super(new DefaultMutableTreeNode("连接"));
    }
    
    public FileTreeModel(TreeNode root) {
        super(root);
    }
    
    public void loadConnections(List<ConnectConfig> configs) {
        this.connections = configs;
        rebuildTree();
    }
    
    public void loadFolders(List<FolderConfig> folders) {
        this.folders = folders;
        rebuildTree();
    }
    
    private void rebuildTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getRoot();
        root.removeAllChildren();
        
        // 添加文件夹节点
        Map<String, DefaultMutableTreeNode> folderNodes = new HashMap<>();
        for (FolderConfig folder : folders) {
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(folder);
            folderNodes.put(folder.getId(), folderNode);
            
            String parentId = folder.getParentId();
            if (parentId == null || parentId.isEmpty()) {
                root.add(folderNode);
            } else {
                DefaultMutableTreeNode parent = folderNodes.get(parentId);
                if (parent != null) {
                    parent.add(folderNode);
                } else {
                    root.add(folderNode);
                }
            }
        }
        
        // 添加连接节点
        for (ConnectConfig config : connections) {
            DefaultMutableTreeNode connNode = new DefaultMutableTreeNode(config);
            String folderId = config.getFolderId();
            
            if (folderId != null && folderNodes.containsKey(folderId)) {
                folderNodes.get(folderId).add(connNode);
            } else {
                root.add(connNode);
            }
        }
        
        reload();
    }
    
    public void addConnection(ConnectConfig config) {
        connections.add(config);
        rebuildTree();
    }
    
    public void removeConnection(ConnectConfig config) {
        connections.remove(config);
        rebuildTree();
    }
    
    public void addFolder(FolderConfig folder) {
        folders.add(folder);
        rebuildTree();
    }
    
    public void removeFolder(FolderConfig folder) {
        folders.remove(folder);
        rebuildTree();
    }
    
    public List<ConnectConfig> getConnections() {
        return connections;
    }
    
    public List<FolderConfig> getFolders() {
        return folders;
    }
}
