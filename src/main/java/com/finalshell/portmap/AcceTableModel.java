package com.finalshell.portmap;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 加速规则表格模型
 * 用于管理端口映射加速规则的表格数据
 */
public class AcceTableModel extends AbstractTableModel {
    
    private static final long serialVersionUID = 1L;
    
    private List<MapRule> rules = new ArrayList<>();
    private String[] columnNames = {"名称", "本地端口", "远程主机", "远程端口", "状态"};
    
    @Override
    public int getRowCount() {
        return rules.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= rules.size()) {
            return null;
        }
        
        MapRule rule = rules.get(rowIndex);
        switch (columnIndex) {
            case 0: return rule.getName();
            case 1: return rule.getLocalPort();
            case 2: return rule.getRemoteHost();
            case 3: return rule.getRemotePort();
            case 4: return rule.isEnabled() ? "启用" : "禁用";
            default: return null;
        }
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 1:
            case 3:
                return Integer.class;
            default:
                return String.class;
        }
    }
    
    public void addRule(MapRule rule) {
        rules.add(rule);
        fireTableRowsInserted(rules.size() - 1, rules.size() - 1);
    }
    
    public void removeRule(int index) {
        if (index >= 0 && index < rules.size()) {
            rules.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public MapRule getRuleAt(int index) {
        if (index >= 0 && index < rules.size()) {
            return rules.get(index);
        }
        return null;
    }
    
    public void updateRule(int index, MapRule rule) {
        if (index >= 0 && index < rules.size()) {
            rules.set(index, rule);
            fireTableRowsUpdated(index, index);
        }
    }
    
    public void setRules(List<MapRule> rules) {
        this.rules = rules != null ? new ArrayList<>(rules) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public List<MapRule> getRules() {
        return new ArrayList<>(rules);
    }
    
    public void clear() {
        int size = rules.size();
        if (size > 0) {
            rules.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    public void refresh() {
        fireTableDataChanged();
    }
}
