package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * 本地文件面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class LocalFilePanel extends JPanel {
    
    private JTree fileTree;
    private DefaultTreeModel treeModel;
    private JTextField pathField;
    private JButton refreshButton;
    private JButton homeButton;
    private JButton upButton;
    
    private File currentDir;
    private List<LocalFileListener> listeners = new ArrayList<>();
    
    public LocalFilePanel() {
        initUI();
        loadRoots();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        pathField = new JTextField();
        pathField.addActionListener(e -> navigateTo(pathField.getText()));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        homeButton = new JButton("主目录");
        upButton = new JButton("上级");
        refreshButton = new JButton("刷新");
        
        homeButton.addActionListener(e -> goHome());
        upButton.addActionListener(e -> goUp());
        refreshButton.addActionListener(e -> refresh());
        
        buttonPanel.add(homeButton);
        buttonPanel.add(upButton);
        buttonPanel.add(refreshButton);
        
        topPanel.add(pathField, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Computer");
        treeModel = new DefaultTreeModel(root);
        fileTree = new JTree(treeModel);
        fileTree.setRootVisible(false);
        fileTree.setShowsRootHandles(true);
        
        fileTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            @Override
            public void treeExpanded(javax.swing.event.TreeExpansionEvent event) {
                loadChildren((DefaultMutableTreeNode) event.getPath().getLastPathComponent());
            }
            
            @Override
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent event) {}
        });
        
        fileTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelected();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(fileTree);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadRoots() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        root.removeAllChildren();
        
        File[] roots = File.listRoots();
        for (File file : roots) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
            node.add(new DefaultMutableTreeNode("loading..."));
            root.add(node);
        }
        
        treeModel.reload();
    }
    
    private void loadChildren(DefaultMutableTreeNode parent) {
        Object userObject = parent.getUserObject();
        if (!(userObject instanceof File)) {
            return;
        }
        
        File dir = (File) userObject;
        if (!dir.isDirectory()) {
            return;
        }
        
        parent.removeAllChildren();
        
        File[] files = dir.listFiles();
        if (files != null) {
            Arrays.sort(files, (a, b) -> {
                if (a.isDirectory() && !b.isDirectory()) return -1;
                if (!a.isDirectory() && b.isDirectory()) return 1;
                return a.getName().compareToIgnoreCase(b.getName());
            });
            
            for (File file : files) {
                if (!file.isHidden()) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                    if (file.isDirectory()) {
                        node.add(new DefaultMutableTreeNode("loading..."));
                    }
                    parent.add(node);
                }
            }
        }
        
        treeModel.nodeStructureChanged(parent);
    }
    
    public void navigateTo(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            currentDir = dir;
            pathField.setText(path);
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            root.removeAllChildren();
            
            File[] files = dir.listFiles();
            if (files != null) {
                Arrays.sort(files, (a, b) -> {
                    if (a.isDirectory() && !b.isDirectory()) return -1;
                    if (!a.isDirectory() && b.isDirectory()) return 1;
                    return a.getName().compareToIgnoreCase(b.getName());
                });
                
                for (File file : files) {
                    if (!file.isHidden()) {
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
                        if (file.isDirectory()) {
                            node.add(new DefaultMutableTreeNode("loading..."));
                        }
                        root.add(node);
                    }
                }
            }
            
            treeModel.reload();
        }
    }
    
    public void goHome() {
        String home = System.getProperty("user.home");
        navigateTo(home);
    }
    
    public void goUp() {
        if (currentDir != null && currentDir.getParentFile() != null) {
            navigateTo(currentDir.getParentFile().getAbsolutePath());
        }
    }
    
    public void refresh() {
        if (currentDir != null) {
            navigateTo(currentDir.getAbsolutePath());
        } else {
            loadRoots();
        }
    }
    
    private void openSelected() {
        TreePath path = fileTree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                File file = (File) userObject;
                if (file.isDirectory()) {
                    navigateTo(file.getAbsolutePath());
                } else {
                    notifyFileSelected(file);
                }
            }
        }
    }
    
    public File getSelectedFile() {
        TreePath path = fileTree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object userObject = node.getUserObject();
            if (userObject instanceof File) {
                return (File) userObject;
            }
        }
        return null;
    }
    
    public File[] getSelectedFiles() {
        TreePath[] paths = fileTree.getSelectionPaths();
        if (paths != null) {
            List<File> files = new ArrayList<>();
            for (TreePath path : paths) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    files.add((File) userObject);
                }
            }
            return files.toArray(new File[0]);
        }
        return new File[0];
    }
    
    public void addListener(LocalFileListener listener) {
        listeners.add(listener);
    }
    
    private void notifyFileSelected(File file) {
        for (LocalFileListener listener : listeners) {
            listener.onFileSelected(file);
        }
    }
    
    public interface LocalFileListener {
        void onFileSelected(File file);
    }
}
