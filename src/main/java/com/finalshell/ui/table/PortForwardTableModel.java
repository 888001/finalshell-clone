package com.finalshell.ui.table;

import com.finalshell.config.PortForwardConfig;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 端口转发表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class PortForwardTableModel extends AbstractTableModel {
    
    private List<PortForwardConfig> configs = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "类型", "本地端口", "远程主机", "远程端口", "状态"
    };
    
    @Override
    public int getRowCount() {
        return configs.size();
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
        if (rowIndex < configs.size()) {
            return configs.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return PortForwardConfig.class;
    }
    
    public void setConfigs(List<PortForwardConfig> configs) {
        this.configs = configs != null ? new ArrayList<>(configs) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addConfig(PortForwardConfig config) {
        configs.add(config);
        fireTableRowsInserted(configs.size() - 1, configs.size() - 1);
    }
    
    public void removeConfig(int index) {
        if (index >= 0 && index < configs.size()) {
            configs.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public PortForwardConfig getConfigAt(int row) {
        if (row >= 0 && row < configs.size()) {
            return configs.get(row);
        }
        return null;
    }
    
    public List<PortForwardConfig> getConfigs() {
        return new ArrayList<>(configs);
    }
}
