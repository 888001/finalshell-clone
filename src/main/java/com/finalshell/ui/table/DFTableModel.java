package com.finalshell.ui.table;

import com.finalshell.monitor.DiskInfo;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 磁盘使用率表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class DFTableModel extends AbstractTableModel {
    
    private List<DiskInfo> diskInfos = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "文件系统", "总大小", "已用", "可用", "使用率", "挂载点"
    };
    
    @Override
    public int getRowCount() {
        return diskInfos.size();
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
        if (rowIndex < diskInfos.size()) {
            return diskInfos.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return DiskInfo.class;
    }
    
    public void setDiskInfos(List<DiskInfo> infos) {
        this.diskInfos = infos != null ? new ArrayList<>(infos) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public DiskInfo getDiskInfoAt(int row) {
        if (row >= 0 && row < diskInfos.size()) {
            return diskInfos.get(row);
        }
        return null;
    }
    
    public List<DiskInfo> getDiskInfos() {
        return new ArrayList<>(diskInfos);
    }
}
