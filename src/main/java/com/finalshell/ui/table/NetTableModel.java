package com.finalshell.ui.table;

import com.finalshell.network.NetRow;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 网络表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class NetTableModel extends AbstractTableModel {
    
    private List<NetRow> rows = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "协议", "本地地址", "本地端口", "远程地址", "远程端口", "状态"
    };
    
    @Override
    public int getRowCount() {
        return rows.size();
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
        if (rowIndex < rows.size()) {
            return rows.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return NetRow.class;
    }
    
    public void setNodes(List<NetRow> rows) {
        this.rows = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public NetRow getRowAt(int index) {
        if (index >= 0 && index < rows.size()) {
            return rows.get(index);
        }
        return null;
    }
    
    public List<NetRow> getRows() {
        return new ArrayList<>(rows);
    }
}
