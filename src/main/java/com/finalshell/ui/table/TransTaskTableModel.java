package com.finalshell.ui.table;

import com.finalshell.transfer.TransTask;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 传输任务表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TransTaskTableModel extends AbstractTableModel {
    
    private List<TransTask> tasks = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "文件名", "大小", "进度", "状态", "速度"
    };
    
    @Override
    public int getRowCount() {
        return tasks.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < tasks.size()) {
            return tasks.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return TransTask.class;
    }
    
    public void addTask(TransTask task) {
        tasks.add(task);
        fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
    }
    
    public void removeTask(TransTask task) {
        int index = tasks.indexOf(task);
        if (index >= 0) {
            tasks.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public void updateTask(TransTask task) {
        int index = tasks.indexOf(task);
        if (index >= 0) {
            fireTableRowsUpdated(index, index);
        }
    }
    
    public TransTask getTaskAt(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }
    
    public TransTask getNextTask() {
        for (int i = tasks.size() - 1; i >= 0; i--) {
            TransTask task = tasks.get(i);
            if (task.getStatus() == TransTask.STATUS_WAITING) {
                return task;
            }
        }
        return null;
    }
    
    public int countWaitingAndRunning() {
        int count = 0;
        for (TransTask task : tasks) {
            int status = task.getStatus();
            if (status == TransTask.STATUS_WAITING || status == TransTask.STATUS_RUNNING) {
                count++;
            }
        }
        return count;
    }
    
    public List<TransTask> getTasks() {
        return new ArrayList<>(tasks);
    }
    
    public void clearCompleted() {
        Iterator<TransTask> it = tasks.iterator();
        int index = 0;
        while (it.hasNext()) {
            TransTask task = it.next();
            if (task.getStatus() == TransTask.STATUS_SUCCESS || 
                task.getStatus() == TransTask.STATUS_ERROR ||
                task.getStatus() == TransTask.STATUS_CANCEL) {
                it.remove();
                fireTableRowsDeleted(index, index);
            } else {
                index++;
            }
        }
    }
}
