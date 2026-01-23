package com.finalshell.ui.table;

import com.finalshell.config.ProxyConfig;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 代理配置表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class ProxyTable extends JTable {
    
    private ProxyTableModel model;
    
    public ProxyTable() {
        model = new ProxyTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(26);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new ProxyRenderer());
        setDefaultRenderer(Boolean.class, new ProxyRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 5) {
            columnModel.getColumn(0).setPreferredWidth(30);  // 选择
            columnModel.getColumn(1).setPreferredWidth(100); // 名称
            columnModel.getColumn(2).setPreferredWidth(60);  // 类型
            columnModel.getColumn(3).setPreferredWidth(150); // 主机
            columnModel.getColumn(4).setPreferredWidth(60);  // 端口
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                
                if (col == 0 && row >= 0) {
                    model.setValueAt(true, row, 0);
                }
                
                if (e.getButton() == MouseEvent.BUTTON3 && row >= 0) {
                    if (!isRowSelected(row)) {
                        setRowSelectionInterval(row, row);
                    }
                }
                
                if (e.getClickCount() == 2 && row >= 0) {
                    ProxyConfig config = model.getConfigAt(row);
                    if (config != null) {
                        firePropertyChange("editProxy", null, config);
                    }
                }
            }
        });
    }
    
    public void setProxies(List<ProxyConfig> proxies) {
        model.setProxies(proxies);
    }
    
    public void addProxy(ProxyConfig proxy) {
        model.addProxy(proxy);
    }
    
    public void removeSelectedProxy() {
        int row = getSelectedRow();
        if (row >= 0) {
            model.removeProxy(row);
        }
    }
    
    public ProxyConfig getSelectedProxy() {
        int row = getSelectedRow();
        return row >= 0 ? model.getConfigAt(row) : null;
    }
    
    public void setCurrentProxy(String proxyId) {
        model.setCurrentProxyId(proxyId);
    }
    
    public ProxyTableModel getProxyModel() {
        return model;
    }
}
