package com.finalshell.process;

import javax.swing.*;
import javax.swing.table.TableModel;

/**
 * Task Table - Custom JTable for process/task display
 */
public class TaskTable extends JTable {
    
    public TaskTable() {
        super();
        initTable();
    }
    
    public TaskTable(TableModel model) {
        super(model);
        initTable();
    }
    
    private void initTable() {
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new java.awt.Dimension(0, 0));
        getTableHeader().setReorderingAllowed(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
    }
    
    public TaskRow getSelectedTask() {
        int row = getSelectedRow();
        if (row >= 0 && getModel() instanceof TaskTableModel) {
            int modelRow = convertRowIndexToModel(row);
            return ((TaskTableModel) getModel()).getTaskAt(modelRow);
        }
        return null;
    }
}
