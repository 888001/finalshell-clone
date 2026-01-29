package com.finalshell.ui.filetree;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.ui.*;
import com.finalshell.util.Tools;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.util.*;

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

        if (userObject instanceof VDir && !isRoot) {
            addSeparator();

            JMenuItem renameItem = new JMenuItem("重命名");
            renameItem.addActionListener(e -> rename());
            add(renameItem);

            JMenuItem deleteItem = new JMenuItem("删除");
            deleteItem.addActionListener(e -> delete());
            add(deleteItem);
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
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof VFile) {
            VFile vfile = (VFile) userObject;
            fileTree.fireConnectEvent(vfile);
        }
    }
    
    private void edit() {
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof VFile) {
            VFile vfile = (VFile) userObject;
            ConnectConfig config = ConfigManager.getInstance().getConnectionById(vfile.getId());
            if (config != null) {
                ConnectionDialog dialog = new ConnectionDialog(
                    (java.awt.Frame) SwingUtilities.getWindowAncestor(fileTree), config);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    fileTree.refreshNode(selectedNode);
                }
            }
        }
    }
    
    private void rename() {
        fileTree.startEditingAtPath(new TreePath(selectedNode.getPath()));
    }
    
    private void delete() {
        int result = JOptionPane.showConfirmDialog(fileTree,
            "确定要删除吗？", "确认删除",
            JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof VFile) {
                VFile vfile = (VFile) userObject;
                ConfigManager.getInstance().deleteConnection(vfile.getId());
                fileTree.refreshTree();
                return;
            }
            if (userObject instanceof VDir) {
                VDir dir = (VDir) userObject;
                ConfigManager.getInstance().removeFolder(dir.getId());
                fileTree.refreshTree();
                return;
            }

            DefaultTreeModel model = fileTree.getTreeModel();
            model.removeNodeFromParent(selectedNode);
        }
    }
    
    private void copyAddress() {
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof VFile) {
            VFile vfile = (VFile) userObject;
            ConnectConfig config = ConfigManager.getInstance().getConnectionById(vfile.getId());
            if (config != null) {
                String address = config.getUserName() + "@" + config.getHost() + ":" + config.getPort();
                Tools.copyToClipboard(address);
            }
        }
    }
    
    private void createSSH() {
        ConnectionDialog dialog = new ConnectionDialog(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(fileTree), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Object userObject = selectedNode.getUserObject();
            if (userObject instanceof VDir) {
                ConnectConfig config = dialog.getConfig();
                config.setParentId(((VDir) userObject).getId());
                ConfigManager.getInstance().saveConnection(config);
            }
            fileTree.refreshTree();
        }
    }
    
    private void createRDP() {
        JOptionPane.showMessageDialog(fileTree, 
            "远程桌面功能待实现", "提示", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void createFolder() {
        com.finalshell.config.FolderConfig folder = new com.finalshell.config.FolderConfig("新建文件夹");
        Object userObject = selectedNode.getUserObject();
        if (userObject instanceof VDir) {
            folder.setParentId(((VDir) userObject).getId());
        }
        ConfigManager.getInstance().addFolder(folder);
        fileTree.refreshTree();
    }
    
    private void sortByName() {
        fileTree.sortChildren(selectedNode, Comparator.comparing(o -> 
            o != null ? o.toString().toLowerCase() : ""));
    }
    
    private void sortByTime() {
        fileTree.sortChildren(selectedNode, (o1, o2) -> {
            if (o1 instanceof VFile && o2 instanceof VFile) {
                return Long.compare(((VFile)o2).getModifyTime(), ((VFile)o1).getModifyTime());
            }
            return 0;
        });
    }
    
    private void importFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导入连接");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON 文件", "json"));
        if (chooser.showOpenDialog(fileTree) == JFileChooser.APPROVE_OPTION) {
            try {
                ConfigManager.getInstance().importConnections(chooser.getSelectedFile());
                fileTree.refreshTree();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(fileTree, "导入失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportSelected() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出选中项");
        chooser.setSelectedFile(new java.io.File("connection.json"));
        if (chooser.showSaveDialog(fileTree) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new java.io.File(file.getAbsolutePath() + ".json");
                }
                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof VFile) {
                    VFile vfile = (VFile) userObject;
                    ConfigManager.getInstance().exportConnection(vfile.getId(), file);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(fileTree, "导出失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportAll() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出全部连接");
        chooser.setSelectedFile(new java.io.File("connections.json"));
        if (chooser.showSaveDialog(fileTree) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new java.io.File(file.getAbsolutePath() + ".json");
                }
                ConfigManager.getInstance().exportConnections(file);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(fileTree, "导出失败: " + e.getMessage(), 
                    "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
