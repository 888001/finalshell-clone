package com.finalshell.sftp;

import javax.swing.tree.*;
import java.util.*;

/**
 * FTP文件树模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FtpFileTreeModel extends DefaultTreeModel {
    
    private FtpClient ftpClient;
    private String rootPath;
    
    public FtpFileTreeModel(FtpClient ftpClient) {
        super(new DefaultMutableTreeNode("Remote"));
        this.ftpClient = ftpClient;
        this.rootPath = "/";
    }
    
    public FtpFileTreeModel(FtpClient ftpClient, String rootPath) {
        super(new DefaultMutableTreeNode(rootPath));
        this.ftpClient = ftpClient;
        this.rootPath = rootPath;
    }
    
    public void loadChildren(DefaultMutableTreeNode parent) {
        if (ftpClient == null || !ftpClient.isConnected()) {
            return;
        }
        
        String path = getPathForNode(parent);
        
        try {
            List<RemoteFile> files = ftpClient.listFiles(path);
            
            parent.removeAllChildren();
            
            List<RemoteFile> dirs = new ArrayList<>();
            List<RemoteFile> regularFiles = new ArrayList<>();
            
            for (RemoteFile file : files) {
                if (file.isDirectory()) {
                    dirs.add(file);
                } else {
                    regularFiles.add(file);
                }
            }
            
            Collections.sort(dirs, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            Collections.sort(regularFiles, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            
            for (RemoteFile dir : dirs) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir);
                node.add(new DefaultMutableTreeNode("loading..."));
                parent.add(node);
            }
            
            for (RemoteFile file : regularFiles) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                parent.add(node);
            }
            
            nodeStructureChanged(parent);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getPathForNode(TreeNode node) {
        if (node == null) {
            return rootPath;
        }
        
        StringBuilder path = new StringBuilder();
        TreeNode[] nodes = getPathToRoot(node);
        
        for (int i = 1; i < nodes.length; i++) {
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) nodes[i];
            Object userObject = treeNode.getUserObject();
            
            if (userObject instanceof RemoteFile) {
                path.append("/").append(((RemoteFile) userObject).getName());
            } else if (userObject instanceof String) {
                String name = (String) userObject;
                if (!name.equals("Remote") && !name.equals("/")) {
                    path.append("/").append(name);
                }
            }
        }
        
        return path.length() > 0 ? path.toString() : rootPath;
    }
    
    public void refresh(DefaultMutableTreeNode node) {
        loadChildren(node);
    }
    
    public void setFtpClient(FtpClient ftpClient) {
        this.ftpClient = ftpClient;
    }
    
    public FtpClient getFtpClient() {
        return ftpClient;
    }
    
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
        ((DefaultMutableTreeNode) getRoot()).setUserObject(rootPath);
        loadChildren((DefaultMutableTreeNode) getRoot());
    }
}
