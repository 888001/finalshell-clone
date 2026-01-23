package com.finalshell.ui.table;

import com.finalshell.network.TracertHop;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 路由跟踪表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TracertTableModel extends AbstractTableModel {
    
    private List<TracertHop> hops = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "跳数", "IP地址", "主机名", "延迟", "位置"
    };
    
    @Override
    public int getRowCount() {
        return hops.size();
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
        if (rowIndex < hops.size()) {
            return hops.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return TracertHop.class;
    }
    
    public void setHops(List<TracertHop> hops) {
        this.hops = hops != null ? new ArrayList<>(hops) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addHop(TracertHop hop) {
        hops.add(hop);
        fireTableRowsInserted(hops.size() - 1, hops.size() - 1);
    }
    
    public void clear() {
        int size = hops.size();
        if (size > 0) {
            hops.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    public TracertHop getHopAt(int row) {
        if (row >= 0 && row < hops.size()) {
            return hops.get(row);
        }
        return null;
    }
    
    public List<TracertHop> getHops() {
        return new ArrayList<>(hops);
    }
}
