package com.finalshell.ui.table;

import com.finalshell.monitor.TopRow;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 任务表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TaskTableModel extends AbstractTableModel {
    
    private List<TopRow> rows = new ArrayList<>();
    private boolean forNetMon;
    private String[] columnNames;
    
    public TaskTableModel() {
        this(false);
    }
    
    public TaskTableModel(boolean forNetMon) {
        this.forNetMon = forNetMon;
        if (forNetMon) {
            columnNames = new String[]{"PID", "进程名", "CPU%", "内存%", "连接数"};
        } else {
            columnNames = new String[]{"PID", "用户", "进程名", "CPU%", "内存%", "状态", "命令"};
        }
    }
    
    @Override
    public int getRowCount() {
        return rows.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < rows.size()) {
            return rows.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return TopRow.class;
    }
    
    public void setNodes(List<TopRow> rows) {
        this.rows = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public TopRow getRowAt(int index) {
        if (index >= 0 && index < rows.size()) {
            return rows.get(index);
        }
        return null;
    }
    
    public int findIndexByPid(int pid) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getPid() == pid) {
                return i;
            }
        }
        return -1;
    }
    
    public List<TopRow> getRows() {
        return new ArrayList<>(rows);
    }
    
    public boolean isForNetMon() {
        return forNetMon;
    }
}
