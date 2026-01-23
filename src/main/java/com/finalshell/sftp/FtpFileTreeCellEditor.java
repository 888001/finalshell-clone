package com.finalshell.sftp;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;

/**
 * FTP文件树单元格编辑器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FtpFileTreeCellEditor extends DefaultTreeCellEditor {
    
    private FtpFileTree fileTree;
    private JTextField editField;
    private String originalName;
    
    public FtpFileTreeCellEditor(FtpFileTree tree, DefaultTreeCellRenderer renderer) {
        super(tree, renderer);
        this.fileTree = tree;
        
        editField = new JTextField();
        editField.setBorder(BorderFactory.createLineBorder(Color.BLUE));
    }
    
    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value,
            boolean isSelected, boolean expanded, boolean leaf, int row) {
        
        if (value instanceof DefaultMutableTreeNode) {
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            if (userObject instanceof RemoteFile) {
                RemoteFile file = (RemoteFile) userObject;
                originalName = file.getName();
                editField.setText(originalName);
            } else {
                originalName = value.toString();
                editField.setText(originalName);
            }
        } else {
            originalName = value.toString();
            editField.setText(originalName);
        }
        
        return editField;
    }
    
    @Override
    public Object getCellEditorValue() {
        return editField.getText();
    }
    
    @Override
    public boolean isCellEditable(EventObject event) {
        if (event instanceof MouseEvent) {
            return ((MouseEvent) event).getClickCount() >= 3;
        }
        return false;
    }
    
    @Override
    public boolean shouldSelectCell(EventObject event) {
        return true;
    }
    
    @Override
    public boolean stopCellEditing() {
        String newName = editField.getText().trim();
        
        if (newName.isEmpty()) {
            cancelCellEditing();
            return false;
        }
        
        if (!newName.equals(originalName)) {
            fileTree.renameSelected(newName);
        }
        
        return super.stopCellEditing();
    }
    
    @Override
    public void cancelCellEditing() {
        editField.setText(originalName);
        super.cancelCellEditing();
    }
}
