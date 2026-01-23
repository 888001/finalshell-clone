package com.finalshell.command;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数列表面板
 * 用于管理快速命令的参数列表
 */
public class ParamListPanel extends JPanel {
    
    private JTable paramTable;
    private ParamTableModel tableModel;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton moveUpBtn;
    private JButton moveDownBtn;
    
    public ParamListPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("参数列表"));
        initComponents();
    }
    
    private void initComponents() {
        // 表格
        tableModel = new ParamTableModel();
        paramTable = new JTable(tableModel);
        paramTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        paramTable.getSelectionModel().addListSelectionListener(e -> updateButtonState());
        
        JScrollPane scrollPane = new JScrollPane(paramTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        addBtn = new JButton("添加");
        addBtn.addActionListener(e -> addParam());
        buttonPanel.add(addBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        
        editBtn = new JButton("编辑");
        editBtn.addActionListener(e -> editParam());
        buttonPanel.add(editBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        
        deleteBtn = new JButton("删除");
        deleteBtn.addActionListener(e -> deleteParam());
        buttonPanel.add(deleteBtn);
        buttonPanel.add(Box.createVerticalStrut(15));
        
        moveUpBtn = new JButton("上移");
        moveUpBtn.addActionListener(e -> moveUp());
        buttonPanel.add(moveUpBtn);
        buttonPanel.add(Box.createVerticalStrut(5));
        
        moveDownBtn = new JButton("下移");
        moveDownBtn.addActionListener(e -> moveDown());
        buttonPanel.add(moveDownBtn);
        
        add(buttonPanel, BorderLayout.EAST);
        
        updateButtonState();
    }
    
    private void updateButtonState() {
        int selectedRow = paramTable.getSelectedRow();
        boolean hasSelection = selectedRow >= 0;
        
        editBtn.setEnabled(hasSelection);
        deleteBtn.setEnabled(hasSelection);
        moveUpBtn.setEnabled(hasSelection && selectedRow > 0);
        moveDownBtn.setEnabled(hasSelection && selectedRow < tableModel.getRowCount() - 1);
    }
    
    private void addParam() {
        String name = JOptionPane.showInputDialog(this, "请输入参数名称:", "添加参数", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            String defaultValue = JOptionPane.showInputDialog(this, "请输入默认值(可选):", "添加参数", JOptionPane.PLAIN_MESSAGE);
            tableModel.addParam(new ParamInfo(name.trim(), defaultValue != null ? defaultValue : ""));
        }
    }
    
    private void editParam() {
        int selectedRow = paramTable.getSelectedRow();
        if (selectedRow >= 0) {
            ParamInfo param = tableModel.getParamAt(selectedRow);
            String name = JOptionPane.showInputDialog(this, "参数名称:", param.getName());
            if (name != null && !name.trim().isEmpty()) {
                String defaultValue = JOptionPane.showInputDialog(this, "默认值:", param.getDefaultValue());
                param.setName(name.trim());
                param.setDefaultValue(defaultValue != null ? defaultValue : "");
                tableModel.fireTableRowsUpdated(selectedRow, selectedRow);
            }
        }
    }
    
    private void deleteParam() {
        int selectedRow = paramTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "确定删除该参数吗?", "确认", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tableModel.removeParam(selectedRow);
            }
        }
    }
    
    private void moveUp() {
        int selectedRow = paramTable.getSelectedRow();
        if (selectedRow > 0) {
            tableModel.moveUp(selectedRow);
            paramTable.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }
    
    private void moveDown() {
        int selectedRow = paramTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount() - 1) {
            tableModel.moveDown(selectedRow);
            paramTable.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }
    
    public List<ParamInfo> getParams() {
        return tableModel.getParams();
    }
    
    public void setParams(List<ParamInfo> params) {
        tableModel.setParams(params);
    }
    
    /**
     * 参数信息类
     */
    public static class ParamInfo {
        private String name;
        private String defaultValue;
        
        public ParamInfo(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
    }
    
    /**
     * 参数表格模型
     */
    private static class ParamTableModel extends AbstractTableModel {
        private List<ParamInfo> params = new ArrayList<>();
        private String[] columns = {"参数名", "默认值"};
        
        @Override
        public int getRowCount() { return params.size(); }
        
        @Override
        public int getColumnCount() { return columns.length; }
        
        @Override
        public String getColumnName(int column) { return columns[column]; }
        
        @Override
        public Object getValueAt(int row, int col) {
            ParamInfo p = params.get(row);
            return col == 0 ? p.getName() : p.getDefaultValue();
        }
        
        public void addParam(ParamInfo param) {
            params.add(param);
            fireTableRowsInserted(params.size() - 1, params.size() - 1);
        }
        
        public void removeParam(int row) {
            params.remove(row);
            fireTableRowsDeleted(row, row);
        }
        
        public ParamInfo getParamAt(int row) { return params.get(row); }
        
        public void moveUp(int row) {
            if (row > 0) {
                ParamInfo temp = params.get(row);
                params.set(row, params.get(row - 1));
                params.set(row - 1, temp);
                fireTableRowsUpdated(row - 1, row);
            }
        }
        
        public void moveDown(int row) {
            if (row < params.size() - 1) {
                ParamInfo temp = params.get(row);
                params.set(row, params.get(row + 1));
                params.set(row + 1, temp);
                fireTableRowsUpdated(row, row + 1);
            }
        }
        
        public List<ParamInfo> getParams() { return new ArrayList<>(params); }
        
        public void setParams(List<ParamInfo> params) {
            this.params = params != null ? new ArrayList<>(params) : new ArrayList<>();
            fireTableDataChanged();
        }
    }
}
