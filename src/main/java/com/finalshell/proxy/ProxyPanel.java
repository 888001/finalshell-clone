package com.finalshell.proxy;

import javax.swing.*;
import java.awt.*;

/**
 * 代理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyPanel extends JPanel {
    
    private ProxyTable proxyTable;
    private ProxyTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton testButton;
    
    public ProxyPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new ProxyTableModel();
        proxyTable = new ProxyTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(proxyTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        testButton = new JButton("测试");
        
        addButton.addActionListener(e -> addProxy());
        editButton.addActionListener(e -> editProxy());
        deleteButton.addActionListener(e -> deleteProxy());
        testButton.addActionListener(e -> testProxy());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(testButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addProxy() {
        AddProxyDialog dialog = new AddProxyDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.addProxy(dialog.getProxyInfo());
        }
    }
    
    private void editProxy() {
        int row = proxyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个代理");
            return;
        }
        ProxyInfo proxy = tableModel.getProxyAt(row);
        AddProxyDialog dialog = new AddProxyDialog(SwingUtilities.getWindowAncestor(this), proxy);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.updateProxy(row, dialog.getProxyInfo());
        }
    }
    
    private void deleteProxy() {
        int row = proxyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个代理");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除此代理?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeProxy(row);
        }
    }
    
    private void testProxy() {
        int row = proxyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个代理");
            return;
        }
        JOptionPane.showMessageDialog(this, "代理测试功能");
    }
}
