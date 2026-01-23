package com.finalshell.portforward;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 端口转发表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"名称", "类型", "本地", "远程", "状态"};
    private List<PFRule> rules;
    
    public PFTableModel() {
        this.rules = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return rules.size();
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
        if (rowIndex < 0 || rowIndex >= rules.size()) return null;
        
        PFRule rule = rules.get(rowIndex);
        switch (columnIndex) {
            case 0: return rule.getName();
            case 1: return rule.getTypeString();
            case 2: return rule.getLocalHost() + ":" + rule.getLocalPort();
            case 3: return rule.getRemoteHost() + ":" + rule.getRemotePort();
            case 4: return rule.isRunning() ? "运行中" : "已停止";
            default: return null;
        }
    }
    
    public void addRule(PFRule rule) {
        rules.add(rule);
        fireTableRowsInserted(rules.size() - 1, rules.size() - 1);
    }
    
    public void removeRule(int row) {
        if (row >= 0 && row < rules.size()) {
            rules.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public void updateRule(int row, PFRule rule) {
        if (row >= 0 && row < rules.size()) {
            rules.set(row, rule);
            fireTableRowsUpdated(row, row);
        }
    }
    
    public PFRule getRuleAt(int row) {
        if (row >= 0 && row < rules.size()) {
            return rules.get(row);
        }
        return null;
    }
    
    public List<PFRule> getRules() {
        return new ArrayList<>(rules);
    }
}
