package com.finalshell.ui.table;

import com.finalshell.hotkey.HotkeyConfig;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 快捷键表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class HotkeyTableModel extends AbstractTableModel {
    
    private List<HotkeyConfig> hotkeys = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "功能", "快捷键", "操作"
    };
    
    @Override
    public int getRowCount() {
        return hotkeys.size();
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
        if (rowIndex < hotkeys.size()) {
            return hotkeys.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return HotkeyConfig.class;
    }
    
    public void setHotkeys(List<HotkeyConfig> hotkeys) {
        this.hotkeys = hotkeys != null ? new ArrayList<>(hotkeys) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addHotkey(HotkeyConfig hotkey) {
        hotkeys.add(hotkey);
        fireTableRowsInserted(hotkeys.size() - 1, hotkeys.size() - 1);
    }
    
    public void removeHotkey(int index) {
        if (index >= 0 && index < hotkeys.size()) {
            hotkeys.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public HotkeyConfig getHotkeyAt(int row) {
        if (row >= 0 && row < hotkeys.size()) {
            return hotkeys.get(row);
        }
        return null;
    }
    
    public List<HotkeyConfig> getHotkeys() {
        return new ArrayList<>(hotkeys);
    }
}
