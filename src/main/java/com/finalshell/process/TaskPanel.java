package com.finalshell.process;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 任务面板
 * 显示进程/任务列表的主面板
 */
public class TaskPanel extends JPanel {
    
    private TaskTable taskTable;
    private TaskTableModel tableModel;
    private JScrollPane scrollPane;
    private JToolBar toolBar;
    private JTextField filterField;
    private TaskDetailPanel detailPanel;
    
    public TaskPanel() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 工具栏
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refresh());
        toolBar.add(refreshBtn);
        
        JButton killBtn = new JButton("结束进程");
        killBtn.addActionListener(e -> killSelectedProcess());
        toolBar.add(killBtn);
        
        toolBar.addSeparator();
        
        toolBar.add(new JLabel("过滤: "));
        filterField = new JTextField(15);
        filterField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });
        toolBar.add(filterField);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 任务表格
        tableModel = new TaskTableModel();
        taskTable = new TaskTable(tableModel);
        taskTable.setRowSorter(new TableRowSorter<>(tableModel));
        
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailPanel();
            }
        });
        
        scrollPane = new JScrollPane(taskTable);
        
        // 分割面板
        detailPanel = new TaskDetailPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, detailPanel);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerLocation(300);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    public void refresh() {
        // 刷新任务列表
        tableModel.refresh();
    }
    
    private void killSelectedProcess() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个进程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = taskTable.convertRowIndexToModel(selectedRow);
        TaskRow task = tableModel.getTaskAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要结束进程 " + task.getName() + " (PID: " + task.getPid() + ") 吗?",
            "确认", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            tableModel.killTask(modelRow);
            refresh();
        }
    }
    
    private void filterTable() {
        String text = filterField.getText().trim();
        TableRowSorter<?> sorter = (TableRowSorter<?>) taskTable.getRowSorter();
        
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    private void updateDetailPanel() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = taskTable.convertRowIndexToModel(selectedRow);
            TaskRow task = tableModel.getTaskAt(modelRow);
            detailPanel.setTask(task);
        } else {
            detailPanel.setTask(null);
        }
    }
    
    public TaskTable getTaskTable() {
        return taskTable;
    }
    
    public TaskTableModel getTableModel() {
        return tableModel;
    }
}
