package com.finalshell.network;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * Traceroute表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"跳数", "IP地址", "主机名", "RTT1", "RTT2", "RTT3"};
    private List<TracertNode> nodes;
    
    public TracertTableModel() {
        this.nodes = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return nodes.size();
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
        if (rowIndex < 0 || rowIndex >= nodes.size()) {
            return null;
        }
        
        TracertNode node = nodes.get(rowIndex);
        switch (columnIndex) {
            case 0: return node.getHop();
            case 1: return node.getIpAddress() != null ? node.getIpAddress() : "*";
            case 2: return node.getHostname() != null ? node.getHostname() : "-";
            case 3: return node.getRtt1() > 0 ? node.getRtt1() + " ms" : "*";
            case 4: return node.getRtt2() > 0 ? node.getRtt2() + " ms" : "*";
            case 5: return node.getRtt3() > 0 ? node.getRtt3() + " ms" : "*";
            default: return null;
        }
    }
    
    public void addNode(TracertNode node) {
        nodes.add(node);
        fireTableRowsInserted(nodes.size() - 1, nodes.size() - 1);
    }
    
    public void clear() {
        nodes.clear();
        fireTableDataChanged();
    }
    
    public TracertNode getNodeAt(int row) {
        if (row >= 0 && row < nodes.size()) {
            return nodes.get(row);
        }
        return null;
    }
    
    public List<TracertNode> getAllNodes() {
        return new ArrayList<>(nodes);
    }
}
