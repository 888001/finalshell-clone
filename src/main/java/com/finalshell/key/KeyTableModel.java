package com.finalshell.key;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 密钥表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class KeyTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"名称", "类型", "位数", "密钥文件", "指纹"};
    private List<KeyInfo> keys;
    
    public KeyTableModel() {
        this.keys = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return keys.size();
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
        if (rowIndex < 0 || rowIndex >= keys.size()) {
            return null;
        }
        
        KeyInfo key = keys.get(rowIndex);
        switch (columnIndex) {
            case 0: return key.getName();
            case 1: return key.getType();
            case 2: return key.getBits();
            case 3: return key.getKeyFile();
            case 4: return key.getFingerprint();
            default: return null;
        }
    }
    
    public void addKey(KeyInfo key) {
        keys.add(key);
        fireTableRowsInserted(keys.size() - 1, keys.size() - 1);
    }
    
    public void removeKey(int row) {
        if (row >= 0 && row < keys.size()) {
            keys.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public void updateKey(int row, KeyInfo key) {
        if (row >= 0 && row < keys.size()) {
            keys.set(row, key);
            fireTableRowsUpdated(row, row);
        }
    }
    
    public KeyInfo getKeyAt(int row) {
        if (row >= 0 && row < keys.size()) {
            return keys.get(row);
        }
        return null;
    }
    
    public List<KeyInfo> getAllKeys() {
        return new ArrayList<>(keys);
    }
    
    public void setKeys(List<KeyInfo> keys) {
        this.keys = new ArrayList<>(keys);
        fireTableDataChanged();
    }
}
