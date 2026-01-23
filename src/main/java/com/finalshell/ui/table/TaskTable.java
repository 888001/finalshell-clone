package com.finalshell.ui.table;

import com.finalshell.monitor.TopRow;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 任务/进程表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class TaskTable extends JTable {
    
    private TaskTableModel model;
    
    public TaskTable() {
        this(false);
    }
    
    public TaskTable(boolean forNetMon) {
        model = new TaskTableModel(forNetMon);
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new TaskCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = getSelectedRow();
                    if (row >= 0) {
                        row = convertRowIndexToModel(row);
                        TopRow topRow = model.getRowAt(row);
                        if (topRow != null) {
                            firePropertyChange("processSelected", null, topRow);
                        }
                    }
                }
            }
        });
    }
    
    public void setNodes(List<TopRow> rows) {
        model.setNodes(rows);
    }
    
    public TopRow getSelectedTopRow() {
        int row = getSelectedRow();
        if (row >= 0) {
            row = convertRowIndexToModel(row);
            return model.getRowAt(row);
        }
        return null;
    }
    
    public int findIndexByPid(int pid) {
        int modelIndex = model.findIndexByPid(pid);
        if (modelIndex >= 0) {
            return convertRowIndexToView(modelIndex);
        }
        return -1;
    }
    
    public TaskTableModel getTaskModel() {
        return model;
    }
}
