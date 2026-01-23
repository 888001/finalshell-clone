package com.finalshell.command;

import javax.swing.table.*;
import java.util.*;

/**
 * 命令表格模型
 */
public class CmdTableModel extends AbstractTableModel {
    
    private String[] columnNames = {"命令", "描述"};
    private List<String[]> data = new ArrayList<>();
    
    @Override
    public int getRowCount() { return data.size(); }
    
    @Override
    public int getColumnCount() { return columnNames.length; }
    
    @Override
    public String getColumnName(int column) { return columnNames[column]; }
    
    @Override
    public Object getValueAt(int row, int column) {
        if (row < data.size()) {
            return data.get(row)[column];
        }
        return null;
    }
    
    public void addCommand(String command) {
        addCommand(command, "");
    }
    
    public void addCommand(String command, String description) {
        data.add(new String[]{command, description});
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }
    
    public void removeCommand(int row) {
        if (row >= 0 && row < data.size()) {
            data.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
    
    public void clear() {
        int size = data.size();
        if (size > 0) {
            data.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
    
    public List<String> getAllCommands() {
        List<String> commands = new ArrayList<>();
        for (String[] row : data) {
            commands.add(row[0]);
        }
        return commands;
    }
}
