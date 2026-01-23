package com.finalshell.portmap;

import javax.swing.*;
import java.awt.*;

/**
 * 端口加速/映射管理面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AcceManagerPanel extends JPanel {
    
    private MapRuleListTable ruleTable;
    private MapRuleListModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton startButton;
    private JButton stopButton;
    
    public AcceManagerPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tableModel = new MapRuleListModel();
        ruleTable = new MapRuleListTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(ruleTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        startButton = new JButton("启动");
        stopButton = new JButton("停止");
        
        addButton.addActionListener(e -> addRule());
        editButton.addActionListener(e -> editRule());
        deleteButton.addActionListener(e -> deleteRule());
        startButton.addActionListener(e -> startMapping());
        stopButton.addActionListener(e -> stopMapping());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addRule() {
        AddMapFrame dialog = new AddMapFrame(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.addRule(dialog.getRule());
        }
    }
    
    private void editRule() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        MapRule rule = tableModel.getRuleAt(row);
        AddMapFrame dialog = new AddMapFrame(SwingUtilities.getWindowAncestor(this), rule);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            tableModel.updateRule(row, dialog.getRule());
        }
    }
    
    private void deleteRule() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定删除此规则?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            tableModel.removeRule(row);
        }
    }
    
    private void startMapping() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        MapRule rule = tableModel.getRuleAt(row);
        rule.setRunning(true);
        tableModel.fireTableRowsUpdated(row, row);
    }
    
    private void stopMapping() {
        int row = ruleTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一条规则");
            return;
        }
        MapRule rule = tableModel.getRuleAt(row);
        rule.setRunning(false);
        tableModel.fireTableRowsUpdated(row, row);
    }
}
