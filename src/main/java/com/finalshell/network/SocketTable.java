package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * 套接字表格
 */
public class SocketTable extends JTable {
    
    private SocketTableModel tableModel;
    
    public SocketTable() {
        tableModel = new SocketTableModel();
        setModel(tableModel);
        initUI();
    }
    
    private void initUI() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(22);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        getTableHeader().setReorderingAllowed(false);
        setDefaultRenderer(Object.class, new SocketCellRenderer());
        setAutoCreateRowSorter(true);
    }
    
    public void setData(java.util.List<SocketRow> rows) {
        tableModel.setData(rows);
    }
    
    public SocketRow getSelectedSocketRow() {
        int row = getSelectedRow();
        if (row >= 0) {
            int modelRow = convertRowIndexToModel(row);
            return tableModel.getRowAt(modelRow);
        }
        return null;
    }
}
