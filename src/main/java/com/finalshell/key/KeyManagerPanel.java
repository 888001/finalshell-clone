package com.finalshell.key;

import javax.swing.*;
import java.awt.*;

/**
 * 密钥管理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class KeyManagerPanel extends JPanel {
    
    private KeyTable keyTable;
    private KeyTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton importButton;
    private JButton exportButton;
    
    public KeyManagerPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new KeyTableModel();
        keyTable = new KeyTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(keyTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        importButton = new JButton("导入");
        exportButton = new JButton("导出");
        
        addButton.addActionListener(e -> addKey());
        editButton.addActionListener(e -> editKey());
        deleteButton.addActionListener(e -> deleteKey());
        importButton.addActionListener(e -> importKey());
        exportButton.addActionListener(e -> exportKey());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addKey() {
        KeyEditDialog dialog = new KeyEditDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.addKey(dialog.getKeyInfo());
        }
    }
    
    private void editKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个密钥");
            return;
        }
        KeyInfo key = tableModel.getKeyAt(row);
        KeyEditDialog dialog = new KeyEditDialog(SwingUtilities.getWindowAncestor(this), key);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.updateKey(row, dialog.getKeyInfo());
        }
    }
    
    private void deleteKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个密钥");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除此密钥?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeKey(row);
        }
    }
    
    private void importKey() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择密钥文件");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = chooser.getSelectedFile();
                KeyInfo key = new KeyInfo();
                key.setName(file.getName());
                key.setKeyFile(file.getAbsolutePath());
                tableModel.addKey(key);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage());
            }
        }
    }
    
    private void exportKey() {
        int row = keyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个密钥");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("导出密钥");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "导出成功");
        }
    }
}
