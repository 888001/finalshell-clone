package com.finalshell.ui.table;

import com.finalshell.transfer.TransTask;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 传输任务表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TransTable extends JTable {
    
    private TransTaskTableModel model;
    
    public TransTable() {
        model = new TransTaskTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(28);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new TransTaskRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 5) {
            columnModel.getColumn(0).setPreferredWidth(200); // 文件名
            columnModel.getColumn(1).setPreferredWidth(80);  // 大小
            columnModel.getColumn(2).setPreferredWidth(100); // 进度
            columnModel.getColumn(3).setPreferredWidth(80);  // 状态
            columnModel.getColumn(4).setPreferredWidth(80);  // 速度
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int row = rowAtPoint(e.getPoint());
                    if (row >= 0 && !isRowSelected(row)) {
                        setRowSelectionInterval(row, row);
                    }
                }
            }
        });
    }
    
    public void addTask(TransTask task) {
        model.addTask(task);
    }
    
    public void removeTask(TransTask task) {
        model.removeTask(task);
    }
    
    public void updateTask(TransTask task) {
        model.updateTask(task);
    }
    
    public TransTask getNextWaitingTask() {
        return model.getNextTask();
    }
    
    public int countWaitingAndRunning() {
        return model.countWaitingAndRunning();
    }
    
    public List<TransTask> getSelectedTasks() {
        int[] rows = getSelectedRows();
        java.util.List<TransTask> tasks = new java.util.ArrayList<>();
        for (int row : rows) {
            int modelRow = convertRowIndexToModel(row);
            TransTask task = model.getTaskAt(modelRow);
            if (task != null) {
                tasks.add(task);
            }
        }
        return tasks;
    }
    
    public TransTaskTableModel getTransModel() {
        return model;
    }
}
