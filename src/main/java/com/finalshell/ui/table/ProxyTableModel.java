package com.finalshell.ui.table;

import com.finalshell.config.ProxyConfig;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 代理配置表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class ProxyTableModel extends AbstractTableModel {
    
    private List<ProxyConfig> proxies = new ArrayList<>();
    private String currentProxyId = null;
    private static final String[] COLUMN_NAMES = {
        "", "名称", "类型", "主机", "端口"
    };
    
    @Override
    public int getRowCount() {
        return proxies.size();
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
        return ProxyConfig.class;
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex >= proxies.size()) return null;
        
        ProxyConfig config = proxies.get(rowIndex);
        if (columnIndex == 0) {
            return config.getId() != null && config.getId().equals(currentProxyId);
        }
        return config;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex < proxies.size()) {
            ProxyConfig config = proxies.get(rowIndex);
            currentProxyId = config.getId();
            fireTableRowsUpdated(0, proxies.size() - 1);
        }
    }
    
    public void setProxies(List<ProxyConfig> proxies) {
        this.proxies = proxies != null ? new ArrayList<>(proxies) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addProxy(ProxyConfig proxy) {
        proxies.add(proxy);
        fireTableRowsInserted(proxies.size() - 1, proxies.size() - 1);
    }
    
    public void removeProxy(int index) {
        if (index >= 0 && index < proxies.size()) {
            proxies.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public ProxyConfig getConfigAt(int row) {
        if (row >= 0 && row < proxies.size()) {
            return proxies.get(row);
        }
        return null;
    }
    
    public void setCurrentProxyId(String id) {
        this.currentProxyId = id;
        fireTableDataChanged();
    }
    
    public String getCurrentProxyId() {
        return currentProxyId;
    }
    
    public List<ProxyConfig> getProxies() {
        return new ArrayList<>(proxies);
    }
}
