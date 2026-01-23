package com.finalshell.command;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 命令列表面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CmdListPanel extends JPanel {
    
    private JTable cmdTable;
    private DefaultTableModel tableModel;
    private JTextField filterField;
    private List<QuickCmd> commands;
    private CommandExecutor executor;
    
    public CmdListPanel() {
        this.commands = new ArrayList<>();
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        
        JPanel topPanel = new JPanel(new BorderLayout(5, 0));
        filterField = new JTextField();
        filterField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterCommands(filterField.getText());
            }
        });
        topPanel.add(new JLabel("过滤:"), BorderLayout.WEST);
        topPanel.add(filterField, BorderLayout.CENTER);
        
        String[] columns = {"名称", "命令", "描述"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        cmdTable = new JTable(tableModel);
        cmdTable.setRowHeight(25);
        cmdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cmdTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeSelected();
                }
            }
        });
        
        TableColumnModel columnModel = cmdTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(150);
        
        JScrollPane scrollPane = new JScrollPane(cmdTable);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton executeBtn = new JButton("执行");
        JButton copyBtn = new JButton("复制");
        
        executeBtn.addActionListener(e -> executeSelected());
        copyBtn.addActionListener(e -> copySelected());
        
        buttonPanel.add(executeBtn);
        buttonPanel.add(copyBtn);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    public void setCommands(List<QuickCmd> commands) {
        this.commands = new ArrayList<>(commands);
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (QuickCmd cmd : commands) {
            tableModel.addRow(new Object[]{cmd.getName(), cmd.getCommand(), cmd.getDescription()});
        }
    }
    
    private void filterCommands(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            refreshTable();
            return;
        }
        
        tableModel.setRowCount(0);
        String lowerKeyword = keyword.toLowerCase();
        for (QuickCmd cmd : commands) {
            if (cmd.getName().toLowerCase().contains(lowerKeyword) ||
                cmd.getCommand().toLowerCase().contains(lowerKeyword)) {
                tableModel.addRow(new Object[]{cmd.getName(), cmd.getCommand(), cmd.getDescription()});
            }
        }
    }
    
    private void executeSelected() {
        int row = cmdTable.getSelectedRow();
        if (row < 0) return;
        
        String command = (String) tableModel.getValueAt(row, 1);
        if (executor != null && command != null) {
            executor.execute(command);
        }
    }
    
    private void copySelected() {
        int row = cmdTable.getSelectedRow();
        if (row < 0) return;
        
        String command = (String) tableModel.getValueAt(row, 1);
        if (command != null) {
            java.awt.datatransfer.StringSelection selection = 
                new java.awt.datatransfer.StringSelection(command);
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        }
    }
    
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }
    
    public interface CommandExecutor {
        void execute(String command);
    }
}
