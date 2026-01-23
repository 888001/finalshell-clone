package com.finalshell.command;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 快捷命令面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCmdPanel extends JPanel {
    
    private JTree cmdTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JTextField searchField;
    private JButton addGroupBtn;
    private JButton addCmdBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton executeBtn;
    private QuickCmdManager cmdManager;
    private CommandExecutor executor;
    
    public QuickCmdPanel() {
        this.cmdManager = QuickCmdManager.getInstance();
        initUI();
        loadCommands();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCommands(searchField.getText());
            }
        });
        topPanel.add(new JLabel("搜索:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        
        rootNode = new DefaultMutableTreeNode("快捷命令");
        treeModel = new DefaultTreeModel(rootNode);
        cmdTree = new JTree(treeModel);
        cmdTree.setRootVisible(false);
        cmdTree.setShowsRootHandles(true);
        cmdTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeSelected();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cmdTree);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addGroupBtn = new JButton("新建分组");
        addCmdBtn = new JButton("新建命令");
        editBtn = new JButton("编辑");
        deleteBtn = new JButton("删除");
        executeBtn = new JButton("执行");
        
        addGroupBtn.addActionListener(e -> addGroup());
        addCmdBtn.addActionListener(e -> addCommand());
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        executeBtn.addActionListener(e -> executeSelected());
        
        buttonPanel.add(addGroupBtn);
        buttonPanel.add(addCmdBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(executeBtn);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCommands() {
        rootNode.removeAllChildren();
        for (QuickCmdGroup group : cmdManager.getGroups()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group);
            for (QuickCmd cmd : group.getCommands()) {
                groupNode.add(new DefaultMutableTreeNode(cmd));
            }
            rootNode.add(groupNode);
        }
        treeModel.reload();
        expandAll();
    }
    
    private void expandAll() {
        for (int i = 0; i < cmdTree.getRowCount(); i++) {
            cmdTree.expandRow(i);
        }
    }
    
    private void filterCommands(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            loadCommands();
            return;
        }
        
        rootNode.removeAllChildren();
        for (QuickCmdGroup group : cmdManager.getGroups()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(group);
            boolean hasMatch = false;
            for (QuickCmd cmd : group.getCommands()) {
                if (cmd.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    cmd.getCommand().toLowerCase().contains(keyword.toLowerCase())) {
                    groupNode.add(new DefaultMutableTreeNode(cmd));
                    hasMatch = true;
                }
            }
            if (hasMatch) {
                rootNode.add(groupNode);
            }
        }
        treeModel.reload();
        expandAll();
    }
    
    private void addGroup() {
        String name = JOptionPane.showInputDialog(this, "请输入分组名称:", "新建分组", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            QuickCmdGroup group = new QuickCmdGroup(name.trim());
            cmdManager.addGroup(group);
            loadCommands();
        }
    }
    
    private void addCommand() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree.getLastSelectedPathComponent();
        QuickCmdGroup group = null;
        
        if (node != null && node.getUserObject() instanceof QuickCmdGroup) {
            group = (QuickCmdGroup) node.getUserObject();
        } else if (node != null && node.getUserObject() instanceof QuickCmd) {
            group = cmdManager.getGroup(((QuickCmd) node.getUserObject()).getGroupId());
        }
        
        if (group == null && cmdManager.getGroups().size() > 0) {
            group = cmdManager.getGroups().get(0);
        }
        
        if (group == null) {
            JOptionPane.showMessageDialog(this, "请先创建分组");
            return;
        }
        
        CreateCmdDialog dialog = new CreateCmdDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            QuickCmd cmd = dialog.getCommand();
            cmdManager.addCommand(group, cmd);
            loadCommands();
        }
    }
    
    private void editSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree.getLastSelectedPathComponent();
        if (node == null) return;
        
        Object obj = node.getUserObject();
        if (obj instanceof QuickCmdGroup) {
            String name = JOptionPane.showInputDialog(this, "分组名称:", ((QuickCmdGroup) obj).getName());
            if (name != null && !name.trim().isEmpty()) {
                ((QuickCmdGroup) obj).setName(name.trim());
                cmdManager.saveCommands();
                loadCommands();
            }
        } else if (obj instanceof QuickCmd) {
            CreateCmdDialog dialog = new CreateCmdDialog(SwingUtilities.getWindowAncestor(this), (QuickCmd) obj);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                cmdManager.updateCommand(dialog.getCommand());
                loadCommands();
            }
        }
    }
    
    private void deleteSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree.getLastSelectedPathComponent();
        if (node == null) return;
        
        int result = JOptionPane.showConfirmDialog(this, "确定删除?", "确认", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) return;
        
        Object obj = node.getUserObject();
        if (obj instanceof QuickCmdGroup) {
            cmdManager.removeGroup((QuickCmdGroup) obj);
        } else if (obj instanceof QuickCmd) {
            QuickCmd cmd = (QuickCmd) obj;
            QuickCmdGroup group = cmdManager.getGroup(cmd.getGroupId());
            if (group != null) {
                cmdManager.removeCommand(group, cmd);
            }
        }
        loadCommands();
    }
    
    private void executeSelected() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) cmdTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof QuickCmd)) return;
        
        QuickCmd cmd = (QuickCmd) node.getUserObject();
        if (executor != null) {
            executor.execute(cmd.getCommand());
        }
    }
    
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    public interface CommandExecutor {
        void execute(String command);
    }
}
