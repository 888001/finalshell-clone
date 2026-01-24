package com.finalshell.process;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Task Table Model - Data model for task/process table
 */
public class TaskTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"PID", "名称", "CPU%", "内存", "状态", "用户"};
    private final List<TaskRow> tasks = new ArrayList<>();
    
    @Override
    public int getRowCount() {
        return tasks.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TaskRow task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return task.getPid();
            case 1: return task.getName();
            case 2: return String.format("%.1f", task.getCpuUsage());
            case 3: return formatMemory(task.getMemoryUsage());
            case 4: return task.getStatus();
            case 5: return task.getUser();
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) return Integer.class;
        return String.class;
    }
    
    public void setTasks(List<TaskRow> newTasks) {
        tasks.clear();
        tasks.addAll(newTasks);
        fireTableDataChanged();
    }
    
    public void addTask(TaskRow task) {
        tasks.add(task);
        fireTableRowsInserted(tasks.size() - 1, tasks.size() - 1);
    }
    
    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public TaskRow getTaskAt(int index) {
        if (index >= 0 && index < tasks.size()) {
            return tasks.get(index);
        }
        return null;
    }
    
    public void clear() {
        int size = tasks.size();
        if (size > 0) {
            tasks.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    private String formatMemory(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
    
    public void refresh() {
        fireTableDataChanged();
    }
    
    public void killTask(int pid) {
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getPid() == pid) {
                removeTask(i);
                break;
            }
        }
    }
    
    public com.finalshell.monitor.TaskInfo getTaskInfoAt(int index) {
        TaskRow row = getTaskAt(index);
        if (row == null) return null;
        com.finalshell.monitor.TaskInfo info = new com.finalshell.monitor.TaskInfo();
        info.setPid(row.getPid());
        info.setName(row.getName());
        info.setUser(row.getUser());
        info.setCpu(row.getCpuUsage());
        info.setMem(row.getMemoryUsage());
        info.setStatus(row.getStatus());
        return info;
    }
}
