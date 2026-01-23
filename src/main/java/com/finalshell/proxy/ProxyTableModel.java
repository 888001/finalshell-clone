package com.finalshell.proxy;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 代理表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ProxyTableModel extends AbstractTableModel {
    
    private static final String[] COLUMNS = {"名称", "类型", "地址", "默认"};
    private List<ProxyInfo> proxies;
    
    public ProxyTableModel() {
        this.proxies = new ArrayList<>();
    }
    
    @Override
    public int getRowCount() {
        return proxies.size();
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
        if (rowIndex < 0 || rowIndex >= proxies.size()) return null;
        
        ProxyInfo proxy = proxies.get(rowIndex);
        switch (columnIndex) {
            case 0: return proxy.getName();
            case 1: return proxy.getTypeString();
            case 2: return proxy.getHost() + ":" + proxy.getPort();
            case 3: return proxy.isDefaultProxy() ? "是" : "";
            default: return null;
        }
    }
    
    public void addProxy(ProxyInfo proxy) {
        proxies.add(proxy);
        fireTableRowsInserted(proxies.size() - 1, proxies.size() - 1);
    }
    
    public void removeProxy(int row) {
        if (row >= 0 && row < proxies.size()) {
            proxies.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public void updateProxy(int row, ProxyInfo proxy) {
        if (row >= 0 && row < proxies.size()) {
            proxies.set(row, proxy);
            fireTableRowsUpdated(row, row);
        }
    }
    
    public ProxyInfo getProxyAt(int row) {
        if (row >= 0 && row < proxies.size()) {
            return proxies.get(row);
        }
        return null;
    }
    
    public List<ProxyInfo> getProxies() {
        return new ArrayList<>(proxies);
    }
}
