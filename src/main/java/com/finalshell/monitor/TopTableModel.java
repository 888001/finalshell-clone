package com.finalshell.monitor;

import javax.swing.table.*;
import java.util.*;

/**
 * 进程TOP表格模型
 */
public class TopTableModel extends AbstractTableModel {
    
    private String[] columnNames = {"PID", "用户", "CPU%", "内存%", "状态", "命令"};
    private List<TopRow> data = new ArrayList<>();
    
    @Override
    public int getRowCount() { return data.size(); }
    
    @Override
    public int getColumnCount() { return columnNames.length; }
    
    @Override
    public String getColumnName(int column) { return columnNames[column]; }
    
    @Override
    public Object getValueAt(int row, int column) {
        if (row >= data.size()) return null;
        TopRow r = data.get(row);
        switch (column) {
            case 0: return r.getPid();
            case 1: return r.getUser();
            case 2: return r.getCpuPercent();
            case 3: return r.getMemPercent();
            case 4: return r.getState();
            case 5: return r.getCommand();
            default: return null;
        }
    }
    
    public void setData(List<TopRow> rows) {
        this.data = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public TopRow getRowAt(int row) {
        return row >= 0 && row < data.size() ? data.get(row) : null;
    }
}
