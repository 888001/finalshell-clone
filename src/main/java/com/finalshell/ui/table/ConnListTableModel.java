package com.finalshell.ui.table;

import com.finalshell.config.ConnectConfig;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 连接列表表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class ConnListTableModel extends AbstractTableModel {
    
    private static final int MAX_ROWS = 30;
    private List<ConnectConfig> connections = new ArrayList<>();
    
    @Override
    public int getRowCount() {
        return Math.min(connections.size(), MAX_ROWS);
    }
    
    @Override
    public int getColumnCount() {
        return 1;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < connections.size()) {
            return connections.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return ConnectConfig.class;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
    
    public void setConnections(List<ConnectConfig> configs) {
        this.connections = configs != null ? new ArrayList<>(configs) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addConnection(ConnectConfig config) {
        connections.add(config);
        fireTableRowsInserted(connections.size() - 1, connections.size() - 1);
    }
    
    public void removeConnection(int index) {
        if (index >= 0 && index < connections.size()) {
            connections.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public ConnectConfig getConfigAt(int row) {
        if (row >= 0 && row < connections.size()) {
            return connections.get(row);
        }
        return null;
    }
    
    public List<ConnectConfig> getConnections() {
        return new ArrayList<>(connections);
    }
}
