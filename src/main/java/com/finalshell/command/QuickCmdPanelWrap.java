package com.finalshell.command;

import javax.swing.*;
import java.awt.*;

/**
 * 快速命令面板包装器
 * 用于包装QuickCmdPanel并提供额外功能
 */
public class QuickCmdPanelWrap extends JPanel {
    
    private QuickCmdPanel quickCmdPanel;
    private JToolBar toolBar;
    private JButton refreshBtn;
    private JButton addBtn;
    private JButton importBtn;
    private JButton exportBtn;
    
    public QuickCmdPanelWrap() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 工具栏
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refresh());
        toolBar.add(refreshBtn);
        
        addBtn = new JButton("添加");
        addBtn.addActionListener(e -> addCommand());
        toolBar.add(addBtn);
        
        toolBar.addSeparator();
        
        importBtn = new JButton("导入");
        importBtn.addActionListener(e -> importCommands());
        toolBar.add(importBtn);
        
        exportBtn = new JButton("导出");
        exportBtn.addActionListener(e -> exportCommands());
        toolBar.add(exportBtn);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 快速命令面板
        quickCmdPanel = new QuickCmdPanel();
        add(quickCmdPanel, BorderLayout.CENTER);
    }
    
    public void refresh() {
        quickCmdPanel.refresh();
    }
    
    private void addCommand() {
        quickCmdPanel.showAddDialog();
    }
    
    private void importCommands() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导入命令");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON文件", "json"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                QuickCmdManager.getInstance().importFromFile(chooser.getSelectedFile());
                refresh();
                JOptionPane.showMessageDialog(this, "导入成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void exportCommands() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出命令");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON文件", "json"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new java.io.File(file.getAbsolutePath() + ".json");
                }
                QuickCmdManager.getInstance().exportToFile(file);
                JOptionPane.showMessageDialog(this, "导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public QuickCmdPanel getQuickCmdPanel() {
        return quickCmdPanel;
    }
}
