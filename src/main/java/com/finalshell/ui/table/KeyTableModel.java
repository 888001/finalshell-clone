package com.finalshell.ui.table;

import com.finalshell.key.SecretKey;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * SSH密钥表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class KeyTableModel extends AbstractTableModel {
    
    private List<SecretKey> keys = new ArrayList<>();
    private String currentKeyId = null;
    private static final String[] COLUMN_NAMES = {
        "", "名称", "类型", "长度"
    };
    
    @Override
    public int getRowCount() {
        return keys.size();
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
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class;
        }
        return SecretKey.class;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= keys.size()) return null;
        
        SecretKey key = keys.get(rowIndex);
        if (columnIndex == 0) {
            return key.getId() != null && key.getId().equals(currentKeyId);
        }
        return key;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex < keys.size()) {
            SecretKey key = keys.get(rowIndex);
            currentKeyId = key.getId();
            fireTableRowsUpdated(0, keys.size() - 1);
        }
    }
    
    public void setKeys(List<SecretKey> keys) {
        this.keys = keys != null ? new ArrayList<>(keys) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addKey(SecretKey key) {
        keys.add(key);
        fireTableRowsInserted(keys.size() - 1, keys.size() - 1);
    }
    
    public void removeKey(int index) {
        if (index >= 0 && index < keys.size()) {
            keys.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public SecretKey getKeyAt(int row) {
        if (row >= 0 && row < keys.size()) {
            return keys.get(row);
        }
        return null;
    }
    
    public void setCurrentKeyId(String id) {
        this.currentKeyId = id;
        fireTableDataChanged();
    }
    
    public String getCurrentKeyId() {
        return currentKeyId;
    }
    
    public List<SecretKey> getKeys() {
        return new ArrayList<>(keys);
    }
}
