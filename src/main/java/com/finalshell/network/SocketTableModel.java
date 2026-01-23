package com.finalshell.network;

import javax.swing.table.*;
import java.util.*;

/**
 * 套接字表格模型
 */
public class SocketTableModel extends AbstractTableModel {
    
    private String[] columnNames = {"协议", "本地地址", "本地端口", "远程地址", "远程端口", "状态"};
    private List<SocketRow> data = new ArrayList<>();
    
    @Override
    public int getRowCount() { return data.size(); }
    
    @Override
    public int getColumnCount() { return columnNames.length; }
    
    @Override
    public String getColumnName(int column) { return columnNames[column]; }
    
    @Override
    public Object getValueAt(int row, int column) {
        if (row >= data.size()) return null;
        SocketRow r = data.get(row);
        switch (column) {
            case 0: return r.getProtocol();
            case 1: return r.getLocalAddress();
            case 2: return r.getLocalPort();
            case 3: return r.getRemoteAddress();
            case 4: return r.getRemotePort();
            case 5: return r.getState();
            default: return null;
        }
    }
    
    public void setData(List<SocketRow> rows) {
        this.data = rows != null ? new ArrayList<>(rows) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public SocketRow getRowAt(int row) {
        return row >= 0 && row < data.size() ? data.get(row) : null;
    }
}
