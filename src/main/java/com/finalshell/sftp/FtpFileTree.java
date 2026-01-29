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
                                openSelected();
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
    
    // Methods needed by FtpTreePopupMenu
    public void openSelected() {
        RemoteFile file = getSelectedFile();
        if (file != null && !file.isDirectory()) {
            try {
                java.io.File tempFile = java.io.File.createTempFile("ftp_", "_" + file.getName());
                tempFile.deleteOnExit();
                if (ftpClient != null) {
                    ftpClient.download(file.getFullPath(), tempFile.getAbsolutePath());
                    java.awt.Desktop.getDesktop().open(tempFile);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "打开文件失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void downloadSelected() {
        List<RemoteFile> files = getSelectedFiles();
        if (!files.isEmpty()) {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("选择下载目录");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                java.io.File destDir = chooser.getSelectedFile();
                for (RemoteFile file : files) {
                    try {
                        String destPath = destDir.getAbsolutePath() + java.io.File.separator + file.getName();
                        if (ftpClient != null) {
                            ftpClient.download(file.getFullPath(), destPath);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "下载失败: " + e.getMessage(), 
                            "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    public void uploadFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择上传文件");
        chooser.setMultiSelectionEnabled(true);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (java.io.File file : chooser.getSelectedFiles()) {
                uploadFile(file);
            }
        }
    }
    
    public void uploadFile(java.io.File file) {
        if (ftpClient != null && file != null && file.exists()) {
            try {
                String remotePath = currentPath + "/" + file.getName();
                ftpClient.upload(file.getAbsolutePath(), remotePath);
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "上传失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void deleteSelected() {
        List<RemoteFile> files = getSelectedFiles();
        if (!files.isEmpty() && ftpClient != null) {
            int result = JOptionPane.showConfirmDialog(this, 
                "确定要删除选中的 " + files.size() + " 个文件？", 
                "确认删除", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                for (RemoteFile file : files) {
                    try {
                        if (file.isDirectory()) {
                            ftpClient.rmdir(file.getFullPath());
                        } else {
                            ftpClient.rm(file.getFullPath());
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "删除失败: " + e.getMessage(), 
                            "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
                refresh();
            }
        }
    }
    
    public void renameSelected(String newName) {
        RemoteFile file = getSelectedFile();
        if (file != null && ftpClient != null && newName != null) {
            try {
                String newPath = currentPath + "/" + newName;
                ftpClient.rename(file.getFullPath(), newPath);
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "重命名失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void createFolder(String folderName) {
        if (ftpClient != null && folderName != null) {
            try {
                String newPath = currentPath + "/" + folderName;
                ftpClient.mkdir(newPath);
                refresh();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "创建文件夹失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public void setPermissions(String permissions) {
        RemoteFile file = getSelectedFile();
        if (file != null && ftpClient != null) {
            try {
                ftpClient.chmod(Integer.parseInt(permissions, 8), file.getFullPath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "设置权限失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public String getSelectedPath() {
        RemoteFile file = getSelectedFile();
        if (file != null) {
            return file.getFullPath();
        }
        return currentPath;
    }
    
    public void showProperties() {
        RemoteFile file = getSelectedFile();
        if (file != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("名称: ").append(file.getName()).append("\n");
            sb.append("路径: ").append(file.getFullPath()).append("\n");
            sb.append("大小: ").append(file.getSize()).append(" bytes\n");
            sb.append("类型: ").append(file.isDirectory() ? "目录" : "文件").append("\n");
            
            JOptionPane.showMessageDialog(this, sb.toString(), "属性", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
