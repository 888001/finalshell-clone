package com.finalshell.portforward;

import javax.swing.*;
import javax.swing.table.TableColumn;

/**
 * 端口转发表格
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFTable extends JTable {
    
    public PFTable(PFTableModel model) {
        super(model);
        initTable();
    }
    
    private void initTable() {
        setRowHeight(25);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setDefaultRenderer(Object.class, new PFRenderer());
        
        TableColumn col0 = getColumnModel().getColumn(0);
        col0.setPreferredWidth(150);
        
        TableColumn col1 = getColumnModel().getColumn(1);
        col1.setPreferredWidth(80);
        
        TableColumn col2 = getColumnModel().getColumn(2);
        col2.setPreferredWidth(150);
        
        TableColumn col3 = getColumnModel().getColumn(3);
        col3.setPreferredWidth(150);
        
        TableColumn col4 = getColumnModel().getColumn(4);
        col4.setPreferredWidth(60);
    }
    
    public PFRule getSelectedRule() {
        int row = getSelectedRow();
        if (row >= 0) {
            return ((PFTableModel) getModel()).getRuleAt(row);
        }
        return null;
    }
}
