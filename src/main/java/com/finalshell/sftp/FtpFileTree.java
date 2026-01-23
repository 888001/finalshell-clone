package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

/**
 * SFTP远程文件树
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - FtpFileTree
 */
public class FtpFileTree extends JTree implements TreeWillExpandListener {
    
    private FtpClient ftpClient;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private Map<String, DefaultMutableTreeNode> nodeCache = new HashMap<>();
    private String currentPath = "/";
    
    public FtpFileTree() {
        rootNode = new DefaultMutableTreeNode("/", true);
        treeModel = new DefaultTreeModel(rootNode);
        setModel(treeModel);
        
        setCellRenderer(new FtpFileTreeCellRenderer());
        setRootVisible(true);
        setShowsRootHandles(true);
        
        addTreeWillExpandListener(this);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = getPathForLocation(e.getX(), e.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = 
                            (DefaultMutableTreeNode) path.getLastPathComponent();
                        Object userObject = node.getUserObject();
                        if (userObject instanceof RemoteFile) {
                            RemoteFile file = (RemoteFile) userObject;
                            if (!file.isDirectory()) {
                                // TODO: 下载或打开文件
                            }
                        }
                    }
                }
            }
        });
    }
    
    public void setFtpClient(FtpClient client) {
        this.ftpClient = client;
    }
    
    public void loadDirectory(String path) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            return;
        }
        
        try {
            List<RemoteFile> files = ftpClient.listFiles(path);
            
            DefaultMutableTreeNode parentNode = nodeCache.get(path);
            if (parentNode == null) {
                parentNode = rootNode;
            }
            
            parentNode.removeAllChildren();
            
            for (RemoteFile file : files) {
                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file);
                if (file.isDirectory()) {
                    childNode.add(new DefaultMutableTreeNode("loading..."));
                }
                parentNode.add(childNode);
                
                String fullPath = path.endsWith("/") ? 
                    path + file.getName() : path + "/" + file.getName();
                nodeCache.put(fullPath, childNode);
            }
            
            treeModel.reload(parentNode);
            currentPath = path;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "加载目录失败: " + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refresh() {
        loadDirectory(currentPath);
    }
    
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        TreePath path = event.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
        Object userObject = node.getUserObject();
        
        if (userObject instanceof RemoteFile) {
            RemoteFile file = (RemoteFile) userObject;
            if (file.isDirectory()) {
                String fullPath = file.getFullPath();
                loadDirectory(fullPath);
            }
        } else if (userObject instanceof String && "/".equals(userObject)) {
            loadDirectory("/");
        }
    }
    
    @Override
    public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
        // 不处理折叠事件
    }
    
    public String getCurrentPath() {
        return currentPath;
    }
    
    public RemoteFile getSelectedFile() {
        TreePath path = getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = 
                (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();
            if (userObject instanceof RemoteFile) {
                return (RemoteFile) userObject;
            }
        }
        return null;
    }
    
    public List<RemoteFile> getSelectedFiles() {
        List<RemoteFile> files = new ArrayList<>();
        TreePath[] paths = getSelectionPaths();
        if (paths != null) {
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = 
                    (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof RemoteFile) {
                    files.add((RemoteFile) userObject);
                }
            }
        }
        return files;
    }
}
