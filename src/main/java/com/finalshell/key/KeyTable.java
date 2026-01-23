package com.finalshell.key;

import javax.swing.*;
import javax.swing.table.*;

/**
 * 密钥表格
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class KeyTable extends JTable {
    
    public KeyTable() {
        this(new KeyTableModel());
    }
    
    public KeyTable(KeyTableModel model) {
        super(model);
        initUI();
    }
    
    private void initUI() {
        setRowHeight(25);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        TableColumnModel columnModel = getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(60);
        columnModel.getColumn(3).setPreferredWidth(200);
        columnModel.getColumn(4).setPreferredWidth(200);
    }
    
    public KeyInfo getSelectedKey() {
        int row = getSelectedRow();
        if (row >= 0) {
            return ((KeyTableModel) getModel()).getKeyAt(row);
        }
        return null;
    }
}
