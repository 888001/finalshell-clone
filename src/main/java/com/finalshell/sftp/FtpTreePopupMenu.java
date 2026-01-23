package com.finalshell.sftp;

import javax.swing.*;
import java.awt.event.*;

/**
 * FTP文件树右键菜单
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FtpTreePopupMenu extends JPopupMenu {
    
    private FtpFileTree fileTree;
    
    private JMenuItem refreshItem;
    private JMenuItem openItem;
    private JMenuItem downloadItem;
    private JMenuItem uploadItem;
    private JMenuItem deleteItem;
    private JMenuItem renameItem;
    private JMenuItem newFolderItem;
    private JMenuItem copyPathItem;
    private JMenuItem propertiesItem;
    
    public FtpTreePopupMenu(FtpFileTree fileTree) {
        this.fileTree = fileTree;
        initMenu();
    }
    
    private void initMenu() {
        refreshItem = new JMenuItem("刷新");
        openItem = new JMenuItem("打开");
        downloadItem = new JMenuItem("下载");
        uploadItem = new JMenuItem("上传");
        deleteItem = new JMenuItem("删除");
        renameItem = new JMenuItem("重命名");
        newFolderItem = new JMenuItem("新建文件夹");
        copyPathItem = new JMenuItem("复制路径");
        propertiesItem = new JMenuItem("属性");
        
        refreshItem.addActionListener(e -> onRefresh());
        openItem.addActionListener(e -> onOpen());
        downloadItem.addActionListener(e -> onDownload());
        uploadItem.addActionListener(e -> onUpload());
        deleteItem.addActionListener(e -> onDelete());
        renameItem.addActionListener(e -> onRename());
        newFolderItem.addActionListener(e -> onNewFolder());
        copyPathItem.addActionListener(e -> onCopyPath());
        propertiesItem.addActionListener(e -> onProperties());
        
        add(refreshItem);
        addSeparator();
        add(openItem);
        add(downloadItem);
        add(uploadItem);
        addSeparator();
        add(newFolderItem);
        add(renameItem);
        add(deleteItem);
        addSeparator();
        add(copyPathItem);
        add(propertiesItem);
    }
    
    private void onRefresh() {
        if (fileTree != null) {
            fileTree.refresh();
        }
    }
    
    private void onOpen() {
        if (fileTree != null) {
            fileTree.openSelected();
        }
    }
    
    private void onDownload() {
        if (fileTree != null) {
            fileTree.downloadSelected();
        }
    }
    
    private void onUpload() {
        if (fileTree != null) {
            fileTree.uploadFile();
        }
    }
    
    private void onDelete() {
        if (fileTree != null) {
            int result = JOptionPane.showConfirmDialog(fileTree, 
                "确定删除选中的文件?", "确认删除", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                fileTree.deleteSelected();
            }
        }
    }
    
    private void onRename() {
        if (fileTree != null) {
            String newName = JOptionPane.showInputDialog(fileTree, "输入新名称:");
            if (newName != null && !newName.trim().isEmpty()) {
                fileTree.renameSelected(newName.trim());
            }
        }
    }
    
    private void onNewFolder() {
        if (fileTree != null) {
            String folderName = JOptionPane.showInputDialog(fileTree, "输入文件夹名称:");
            if (folderName != null && !folderName.trim().isEmpty()) {
                fileTree.createFolder(folderName.trim());
            }
        }
    }
    
    private void onCopyPath() {
        if (fileTree != null) {
            String path = fileTree.getSelectedPath();
            if (path != null) {
                java.awt.datatransfer.StringSelection selection = 
                    new java.awt.datatransfer.StringSelection(path);
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(selection, null);
            }
        }
    }
    
    private void onProperties() {
        if (fileTree != null) {
            fileTree.showProperties();
        }
    }
    
    public void updateMenuState(boolean hasSelection, boolean isDirectory) {
        openItem.setEnabled(hasSelection);
        downloadItem.setEnabled(hasSelection && !isDirectory);
        deleteItem.setEnabled(hasSelection);
        renameItem.setEnabled(hasSelection);
        copyPathItem.setEnabled(hasSelection);
        propertiesItem.setEnabled(hasSelection);
    }
}
