package com.finalshell.ui.table;

import com.finalshell.network.TracertHop;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * 路由跟踪表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TracertTable extends JTable {
    
    private TracertTableModel model;
    
    public TracertTable() {
        model = new TracertTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new TracertCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 5) {
            columnModel.getColumn(0).setPreferredWidth(40);  // 跳数
            columnModel.getColumn(1).setPreferredWidth(150); // IP地址
            columnModel.getColumn(2).setPreferredWidth(150); // 主机名
            columnModel.getColumn(3).setPreferredWidth(80);  // 延迟
            columnModel.getColumn(4).setPreferredWidth(150); // 位置
        }
    }
    
    public void addHop(TracertHop hop) {
        model.addHop(hop);
    }
    
    public void setHops(List<TracertHop> hops) {
        model.setHops(hops);
    }
    
    public void clear() {
        model.clear();
    }
    
    public TracertHop getSelectedHop() {
        int row = getSelectedRow();
        return row >= 0 ? model.getHopAt(row) : null;
    }
    
    public TracertTableModel getTracertModel() {
        return model;
    }
}
