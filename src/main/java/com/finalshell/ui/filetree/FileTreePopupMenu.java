package com.finalshell.ui.filetree;

import com.finalshell.ui.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;

/**
 * 文件树右键菜单
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FileTree_UI_DeepAnalysis.md - FileTreePopupMenu
 */
public class FileTreePopupMenu extends JPopupMenu {
    
    private FileTree fileTree;
    private DefaultMutableTreeNode selectedNode;
    private boolean isRoot;
    private boolean isSearchMode;
    
    public FileTreePopupMenu(FileTree fileTree, DefaultMutableTreeNode node, 
            boolean isRoot, boolean isSearchMode) {
        this.fileTree = fileTree;
        this.selectedNode = node;
        this.isRoot = isRoot;
        this.isSearchMode = isSearchMode;
        
        initMenuItems();
    }
    
    private void initMenuItems() {
        Object userObject = selectedNode.getUserObject();
        
        // 连接菜单项
        if (userObject instanceof VFile && !isRoot) {
            JMenuItem connectItem = new JMenuItem("连接");
            connectItem.addActionListener(e -> connect());
            add(connectItem);
            
            addSeparator();
            
            JMenuItem editItem = new JMenuItem("编辑");
            editItem.addActionListener(e -> edit());
            add(editItem);
            
            JMenuItem renameItem = new JMenuItem("重命名");
            renameItem.addActionListener(e -> rename());
            add(renameItem);
            
            JMenuItem deleteItem = new JMenuItem("删除");
            deleteItem.addActionListener(e -> delete());
            add(deleteItem);
            
            addSeparator();
            
            JMenuItem copyAddressItem = new JMenuItem("复制地址");
            copyAddressItem.addActionListener(e -> copyAddress());
            add(copyAddressItem);
        }
        
        // 新建子菜单
        JMenu newMenu = new JMenu("新建");
        
        JMenuItem newSSHItem = new JMenuItem("SSH连接(Linux)");
        newSSHItem.addActionListener(e -> createSSH());
        newMenu.add(newSSHItem);
        
        JMenuItem newRDPItem = new JMenuItem("远程桌面连接(Windows)");
        newRDPItem.addActionListener(e -> createRDP());
        newMenu.add(newRDPItem);
        
        newMenu.addSeparator();
        
        JMenuItem newFolderItem = new JMenuItem("文件夹");
        newFolderItem.addActionListener(e -> createFolder());
        newMenu.add(newFolderItem);
        
        add(newMenu);
        
        // 排序子菜单
        JMenu sortMenu = new JMenu("排序");
        JMenuItem sortByNameItem = new JMenuItem("按名称");
        sortByNameItem.addActionListener(e -> sortByName());
        sortMenu.add(sortByNameItem);
        
        JMenuItem sortByTimeItem = new JMenuItem("按时间");
        sortByTimeItem.addActionListener(e -> sortByTime());
        sortMenu.add(sortByTimeItem);
        
        add(sortMenu);
        
        addSeparator();
        
        // 导入导出
        JMenu importMenu = new JMenu("导入");
        JMenuItem importFromFileItem = new JMenuItem("从文件...");
        importFromFileItem.addActionListener(e -> importFromFile());
        importMenu.add(importFromFileItem);
        add(importMenu);
        
        JMenu exportMenu = new JMenu("导出");
        JMenuItem exportSelectedItem = new JMenuItem("选中...");
        exportSelectedItem.addActionListener(e -> exportSelected());
        exportMenu.add(exportSelectedItem);
        
        JMenuItem exportAllItem = new JMenuItem("全部...");
        exportAllItem.addActionListener(e -> exportAll());
        exportMenu.add(exportAllItem);
        
        add(exportMenu);
    }
    
    private void connect() {
        // TODO: 连接到选中的服务器
    }
    
    private void edit() {
        // TODO: 编辑连接配置
    }
    
    private void rename() {
        fileTree.startEditingAtPath(new TreePath(selectedNode.getPath()));
    }
    
    private void delete() {
        int result = JOptionPane.showConfirmDialog(fileTree,
            "确定要删除吗？", "确认删除",
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            DefaultTreeModel model = fileTree.getTreeModel();
            model.removeNodeFromParent(selectedNode);
        }
    }
    
    private void copyAddress() {
        // TODO: 复制主机地址到剪贴板
    }
    
    private void createSSH() {
        // TODO: 新建SSH连接
    }
    
    private void createRDP() {
        // TODO: 新建RDP连接
    }
    
    private void createFolder() {
        VDir dir = new VDir("新建文件夹");
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(dir);
        DefaultTreeModel model = fileTree.getTreeModel();
        model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());
        fileTree.startEditingAtPath(new TreePath(newNode.getPath()));
    }
    
    private void sortByName() {
        // TODO: 按名称排序
    }
    
    private void sortByTime() {
        // TODO: 按时间排序
    }
    
    private void importFromFile() {
        // TODO: 从文件导入
    }
    
    private void exportSelected() {
        // TODO: 导出选中项
    }
    
    private void exportAll() {
        // TODO: 导出全部
    }
}
