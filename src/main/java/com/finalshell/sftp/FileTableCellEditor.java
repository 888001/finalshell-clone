package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;

/**
 * 文件表格单元格编辑器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FileTableCellEditor extends AbstractCellEditor implements TableCellEditor {
    
    private JTextField textField;
    private Object originalValue;
    private FileTableCellEditorListener listener;
    
    public interface FileTableCellEditorListener {
        void onRename(Object oldValue, Object newValue);
    }
    
    public FileTableCellEditor() {
        textField = new JTextField();
        textField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    stopCellEditing();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cancelCellEditing();
                }
            }
        });
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                stopCellEditing();
            }
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        originalValue = value;
        textField.setText(value != null ? value.toString() : "");
        textField.selectAll();
        return textField;
    }
    
    @Override
    public Object getCellEditorValue() {
        return textField.getText();
    }
    
    @Override
    public boolean stopCellEditing() {
        Object newValue = getCellEditorValue();
        if (listener != null && !newValue.equals(originalValue)) {
            listener.onRename(originalValue, newValue);
        }
        return super.stopCellEditing();
    }
    
    public void setListener(FileTableCellEditorListener listener) {
        this.listener = listener;
    }
}
