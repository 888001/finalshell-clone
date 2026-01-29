package com.finalshell.ui;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 全部连接面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - AllPanel
 */
public class AllPanel extends JPanel {
    
    private JTree fileTree;
    private JToolBar toolbar;
    private OpenPanel openPanel;
    private boolean forSearch;
    private JScrollPane scrollPane;
    private List<ConnectConfig> allConfigs = new ArrayList<>();
    private List<ConnectConfig> filteredConfigs = new ArrayList<>();
    
    public AllPanel(OpenPanel openPanel, boolean forSearch) {
        this.openPanel = openPanel;
        this.forSearch = forSearch;
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 文件树
        fileTree = new JTree();
        fileTree.setRootVisible(false);
        fileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) return;
                TreePath path = fileTree.getSelectionPath();
                if (path == null) return;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                Object userObject = node.getUserObject();
                if (userObject instanceof ConnectConfig) {
                    openPanel.openConnection((ConnectConfig) userObject);
                }
            }
        });
        
        scrollPane = new JScrollPane(fileTree);
        add(scrollPane, BorderLayout.CENTER);
        
        if (!forSearch) {
            // 工具栏
            toolbar = new JToolBar();
            toolbar.setFloatable(false);
            
            JButton newBtn = new JButton("新建");
            JButton editBtn = new JButton("编辑");
            JButton deleteBtn = new JButton("删除");
            
            toolbar.add(newBtn);
            toolbar.add(editBtn);
            toolbar.add(deleteBtn);
            
            add(toolbar, BorderLayout.SOUTH);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // 渐变背景
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0, 0, new Color(250, 250, 255),
            0, getHeight(), new Color(240, 240, 250));
        g2.setPaint(gp);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
    
    public void setConfigs(List<ConnectConfig> configs) {
        this.allConfigs = new ArrayList<>(configs);
        this.filteredConfigs = new ArrayList<>(configs);
        refreshTree();
    }
    
    public void filter(String keyword) {
        filteredConfigs.clear();
        String lower = keyword.toLowerCase();
        
        for (ConnectConfig config : allConfigs) {
            String name = config.getName();
            String host = config.getHost();
            if ((name != null && name.toLowerCase().contains(lower)) ||
                (host != null && host.toLowerCase().contains(lower))) {
                filteredConfigs.add(config);
            }
        }
        
        refreshTree();
    }
    
    private void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("连接");
        
        for (ConnectConfig config : filteredConfigs) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(config);
            root.add(node);
        }
        
        DefaultTreeModel model = new DefaultTreeModel(root);
        fileTree.setModel(model);
        
        // 展开所有节点
        for (int i = 0; i < fileTree.getRowCount(); i++) {
            fileTree.expandRow(i);
        }
    }
}
