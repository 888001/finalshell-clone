package com.finalshell.monitor;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * 进程TOP表格
 */
public class TopTable extends JTable {
    
    private TopTableModel tableModel;
    
    public TopTable() {
        tableModel = new TopTableModel();
        setModel(tableModel);
        initUI();
    }
    
    private void initUI() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(22);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        getTableHeader().setReorderingAllowed(false);
        setDefaultRenderer(Object.class, new TopCellRenderer());
        setAutoCreateRowSorter(true);
    }
    
    public void setData(java.util.List<TopRow> rows) {
        tableModel.setData(rows);
    }
    
    public TopRow getSelectedRow() {
        int row = getSelectedRow();
        if (row >= 0) {
            int modelRow = convertRowIndexToModel(row);
            return tableModel.getRowAt(modelRow);
        }
        return null;
    }
}
