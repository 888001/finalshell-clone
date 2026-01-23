package com.finalshell.sftp;

import com.finalshell.ui.ImageManager;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

/**
 * FTP文件树单元格渲染器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - FtpFileTreeCellRenderer
 */
public class FtpFileTreeCellRenderer extends DefaultTreeCellRenderer {
    
    private ImageManager imageManager;
    
    public FtpFileTreeCellRenderer() {
        imageManager = ImageManager.getInstance();
    }
    
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            
            if (userObject instanceof RemoteFile) {
                RemoteFile file = (RemoteFile) userObject;
                setText(file.getName());
                
                if (file.isDirectory()) {
                    setIcon(imageManager.getIcon("folder", 16, 16));
                } else if (file.isLink()) {
                    setIcon(imageManager.getIcon("file", 16, 16));
                } else {
                    setIcon(getFileIcon(file.getName()));
                }
            } else if (userObject instanceof String) {
                setText((String) userObject);
                if ("/".equals(userObject)) {
                    setIcon(imageManager.getIcon("folder", 16, 16));
                }
            }
        }
        
        return this;
    }
    
    private Icon getFileIcon(String fileName) {
        String ext = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            ext = fileName.substring(dotIndex + 1).toLowerCase();
        }
        
        // 根据扩展名选择图标
        switch (ext) {
            case "txt":
            case "log":
            case "md":
                return imageManager.getIcon("file", 16, 16);
            case "sh":
            case "bash":
            case "py":
            case "java":
            case "js":
                return imageManager.getIcon("file", 16, 16);
            default:
                return imageManager.getIcon("file", 16, 16);
        }
    }
}
