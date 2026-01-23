package com.finalshell.command;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 命令表格
 * 用于显示命令列表
 */
public class CmdTable extends JTable {
    
    private CmdTableModel tableModel;
    
    public CmdTable() {
        tableModel = new CmdTableModel();
        setModel(tableModel);
        initUI();
    }
    
    private void initUI() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        
        getTableHeader().setReorderingAllowed(false);
        
        setDefaultRenderer(Object.class, new CommandRender());
    }
    
    public void addCommand(String command) {
        tableModel.addCommand(command);
    }
    
    public void addCommand(String command, String description) {
        tableModel.addCommand(command, description);
    }
    
    public void removeSelectedCommand() {
        int row = getSelectedRow();
        if (row >= 0) {
            tableModel.removeCommand(row);
        }
    }
    
    public String getSelectedCommand() {
        int row = getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public void clearCommands() {
        tableModel.clear();
    }
    
    public java.util.List<String> getAllCommands() {
        return tableModel.getAllCommands();
    }
}
