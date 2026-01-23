package com.finalshell.ui.panel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * SSH端口转发面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SshForwardingPanel extends JPanel {
    
    private JTable forwardTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    public SshForwardingPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("端口转发"));
        
        String[] columns = {"类型", "本地端口", "远程主机", "远程端口", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        forwardTable = new JTable(tableModel);
        forwardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        forwardTable.setRowHeight(25);
        
        TableColumnModel columnModel = forwardTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(60);
        
        JScrollPane scrollPane = new JScrollPane(forwardTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("添加");
        editButton = new JButton("编辑");
        deleteButton = new JButton("删除");
        
        addButton.addActionListener(e -> addForward());
        editButton.addActionListener(e -> editForward());
        deleteButton.addActionListener(e -> deleteForward());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void addForward() {
        String[] types = {"本地 -> 远程", "远程 -> 本地", "动态(SOCKS)"};
        String type = (String) JOptionPane.showInputDialog(this, "转发类型:", "添加端口转发",
            JOptionPane.QUESTION_MESSAGE, null, types, types[0]);
        
        if (type != null) {
            JTextField localPortField = new JTextField("8080");
            JTextField remoteHostField = new JTextField("localhost");
            JTextField remotePortField = new JTextField("80");
            
            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("本地端口:"));
            panel.add(localPortField);
            panel.add(new JLabel("远程主机:"));
            panel.add(remoteHostField);
            panel.add(new JLabel("远程端口:"));
            panel.add(remotePortField);
            
            int result = JOptionPane.showConfirmDialog(this, panel, "端口转发配置",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                tableModel.addRow(new Object[]{
                    type, localPortField.getText(), remoteHostField.getText(),
                    remotePortField.getText(), "停止"
                });
            }
        }
    }
    
    private void editForward() {
        int row = forwardTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个转发规则");
            return;
        }
        
        JTextField localPortField = new JTextField((String) tableModel.getValueAt(row, 1));
        JTextField remoteHostField = new JTextField((String) tableModel.getValueAt(row, 2));
        JTextField remotePortField = new JTextField((String) tableModel.getValueAt(row, 3));
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("本地端口:"));
        panel.add(localPortField);
        panel.add(new JLabel("远程主机:"));
        panel.add(remoteHostField);
        panel.add(new JLabel("远程端口:"));
        panel.add(remotePortField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "编辑端口转发",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            tableModel.setValueAt(localPortField.getText(), row, 1);
            tableModel.setValueAt(remoteHostField.getText(), row, 2);
            tableModel.setValueAt(remotePortField.getText(), row, 3);
        }
    }
    
    private void deleteForward() {
        int row = forwardTable.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
        }
    }
    
    public List<String[]> getForwards() {
        List<String[]> forwards = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            forwards.add(new String[]{
                (String) tableModel.getValueAt(i, 0),
                (String) tableModel.getValueAt(i, 1),
                (String) tableModel.getValueAt(i, 2),
                (String) tableModel.getValueAt(i, 3)
            });
        }
        return forwards;
    }
    
    public void setForwards(List<String[]> forwards) {
        tableModel.setRowCount(0);
        for (String[] forward : forwards) {
            tableModel.addRow(new Object[]{forward[0], forward[1], forward[2], forward[3], "停止"});
        }
    }
}
