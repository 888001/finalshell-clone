package com.finalshell.ui.table;

import com.finalshell.network.NetRow;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * 网络监控表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class NetTable extends JTable {
    
    private NetTableModel model;
    
    public NetTable() {
        model = new NetTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(22);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new NetCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        // 设置列宽
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 6) {
            columnModel.getColumn(0).setPreferredWidth(80);  // 协议
            columnModel.getColumn(1).setPreferredWidth(150); // 本地地址
            columnModel.getColumn(2).setPreferredWidth(60);  // 本地端口
            columnModel.getColumn(3).setPreferredWidth(150); // 远程地址
            columnModel.getColumn(4).setPreferredWidth(60);  // 远程端口
            columnModel.getColumn(5).setPreferredWidth(80);  // 状态
        }
    }
    
    public void setNodes(List<NetRow> rows) {
        model.setNodes(rows);
    }
    
    public NetRow getSelectedNetRow() {
        int row = getSelectedRow();
        if (row >= 0) {
            row = convertRowIndexToModel(row);
            return model.getRowAt(row);
        }
        return null;
    }
    
    public NetTableModel getNetModel() {
        return model;
    }
    
    public void refresh() {
        model.fireTableDataChanged();
    }
    
    // Note: getSelectedRow() returns int from JTable parent, use getSelectedNetRow() for NetRow
}
