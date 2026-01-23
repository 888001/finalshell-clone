package com.finalshell.portforward;

import javax.swing.*;
import java.awt.*;

/**
 * 端口转发面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFPanel extends JPanel {
    
    private PFTable pfTable;
    private PFTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton stopButton;
    
    public PFPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new PFTableModel();
        pfTable = new PFTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(pfTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        startButton = new JButton("启动");
        stopButton = new JButton("停止");
        
        addButton.addActionListener(e -> addForward());
        editButton.addActionListener(e -> editForward());
        deleteButton.addActionListener(e -> deleteForward());
        startButton.addActionListener(e -> startForward());
        stopButton.addActionListener(e -> stopForward());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addForward() {
        AddPFDialog dialog = new AddPFDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.addRule(dialog.getRule());
        }
    }
    
    private void editForward() {
        int row = pfTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        PFRule rule = tableModel.getRuleAt(row);
        AddPFDialog dialog = new AddPFDialog(SwingUtilities.getWindowAncestor(this), rule);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.updateRule(row, dialog.getRule());
        }
    }
    
    private void deleteForward() {
        int row = pfTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除此规则?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeRule(row);
        }
    }
    
    private void startForward() {
        int row = pfTable.getSelectedRow();
        if (row >= 0) {
            PFRule rule = tableModel.getRuleAt(row);
            rule.setRunning(true);
            tableModel.fireTableRowsUpdated(row, row);
        }
    }
    
    private void stopForward() {
        int row = pfTable.getSelectedRow();
        if (row >= 0) {
            PFRule rule = tableModel.getRuleAt(row);
            rule.setRunning(false);
            tableModel.fireTableRowsUpdated(row, row);
        }
    }
}
