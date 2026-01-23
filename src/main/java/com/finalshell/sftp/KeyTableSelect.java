package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

/**
 * 键盘表格选择
 * 支持键盘导航选择表格行
 */
public class KeyTableSelect extends KeyAdapter {
    
    private JTable table;
    private StringBuilder searchBuffer = new StringBuilder();
    private long lastKeyTime = 0;
    private static final long KEY_TIMEOUT = 1000;
    
    public KeyTableSelect(JTable table) {
        this.table = table;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (!Character.isLetterOrDigit(c)) return;
        
        long now = System.currentTimeMillis();
        if (now - lastKeyTime > KEY_TIMEOUT) {
            searchBuffer.setLength(0);
        }
        lastKeyTime = now;
        searchBuffer.append(Character.toLowerCase(c));
        
        String search = searchBuffer.toString();
        int startRow = table.getSelectedRow() + 1;
        int rowCount = table.getRowCount();
        
        for (int i = 0; i < rowCount; i++) {
            int row = (startRow + i) % rowCount;
            Object value = table.getValueAt(row, 0);
            if (value != null && value.toString().toLowerCase().startsWith(search)) {
                table.setRowSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
                break;
            }
        }
    }
    
    public void reset() {
        searchBuffer.setLength(0);
    }
}
