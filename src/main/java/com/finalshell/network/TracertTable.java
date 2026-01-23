package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.*;

/**
 * Traceroute表格
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TracertTable extends JTable {
    
    public TracertTable() {
        this(new TracertTableModel());
    }
    
    public TracertTable(TracertTableModel model) {
        super(model);
        initUI();
    }
    
    private void initUI() {
        setRowHeight(25);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        TableColumnModel columnModel = getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(150);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(80);
        columnModel.getColumn(5).setPreferredWidth(80);
        
        setDefaultRenderer(Object.class, new TracertCellRenderer());
    }
    
    public TracertNode getSelectedNode() {
        int row = getSelectedRow();
        if (row >= 0) {
            return ((TracertTableModel) getModel()).getNodeAt(row);
        }
        return null;
    }
}
