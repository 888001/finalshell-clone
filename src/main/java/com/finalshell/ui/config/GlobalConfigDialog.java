package com.finalshell.ui.config;

import com.finalshell.ui.CardPanel;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
 * 全局配置对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: GlobalConfig_UI_DeepAnalysis.md - GlobalConfigDialog
 */
public class GlobalConfigDialog extends JFrame {
    
    private static boolean isShowing = false;
    
    private CardPanel cardPanel;
    private JTree tree;
    private java.util.List<ConfigPanel> configPanelList = new ArrayList<>();
    
    private GeneralConfigPanel generalConfigPanel;
    private TerminalConfigPanel terminalConfigPanel;
    private HotkeyConfigPanel hotkeyPanel;
    
    public GlobalConfigDialog() {
        super("设置");
        initComponents();
        setSize(700, 500);
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        
        // 左侧：配置树
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(180, 0));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("设置");
        DefaultMutableTreeNode generalNode = new DefaultMutableTreeNode("常规");
        DefaultMutableTreeNode terminalNode = new DefaultMutableTreeNode("终端");
        DefaultMutableTreeNode hotkeyNode = new DefaultMutableTreeNode("快捷键");
        DefaultMutableTreeNode themeNode = new DefaultMutableTreeNode("主题");
        DefaultMutableTreeNode fontNode = new DefaultMutableTreeNode("字体");
        
        root.add(generalNode);
        root.add(terminalNode);
        terminalNode.add(hotkeyNode);
        terminalNode.add(themeNode);
        terminalNode.add(fontNode);
        
        tree = new JTree(root);
        tree.setRootVisible(false);
        tree.expandRow(0);
        tree.expandRow(1);
        
        tree.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            Object node = path.getLastPathComponent();
            showConfigPanel(node.toString());
        });
        
        leftPanel.add(new JScrollPane(tree), BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);
        
        // 右侧：配置面板
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        
        cardPanel = new CardPanel();
        
        generalConfigPanel = new GeneralConfigPanel();
        terminalConfigPanel = new TerminalConfigPanel();
        hotkeyPanel = new HotkeyConfigPanel();
        
        cardPanel.addCard(generalConfigPanel, "常规");
        cardPanel.addCard(terminalConfigPanel, "终端");
        cardPanel.addCard(hotkeyPanel, "快捷键");
        cardPanel.addCard(new JPanel(), "主题");
        cardPanel.addCard(new JPanel(), "字体");
        
        configPanelList.add(generalConfigPanel);
        configPanelList.add(terminalConfigPanel);
        configPanelList.add(hotkeyPanel);
        
        rightPanel.add(cardPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
        
        // 底部：按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("确定");
        JButton cancelButton = new JButton("取消");
        JButton applyButton = new JButton("应用");
        
        okButton.addActionListener(e -> {
            applyAll();
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        applyButton.addActionListener(e -> applyAll());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // 默认显示常规配置
        cardPanel.show("常规");
    }
    
    private void showConfigPanel(String name) {
        cardPanel.show(name);
    }
    
    private void applyAll() {
        for (ConfigPanel panel : configPanelList) {
            panel.apply();
        }
    }
    
    public static void showDialog(Window owner) {
        if (isShowing) return;
        
        isShowing = true;
        GlobalConfigDialog dialog = new GlobalConfigDialog();
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                isShowing = false;
            }
        });
        dialog.setVisible(true);
    }
}
