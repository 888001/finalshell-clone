package com.finalshell.ui.table;

import com.finalshell.sftp.RemoteFile;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * 文件列表表格模型
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class FileTableModel extends AbstractTableModel {
    
    private List<RemoteFile> files = new ArrayList<>();
    private static final String[] COLUMN_NAMES = {
        "名称", "大小", "类型", "修改时间", "权限"
    };
    
    @Override
    public int getRowCount() {
        return files.size();
    }
    
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < files.size()) {
            return files.get(rowIndex);
        }
        return null;
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return RemoteFile.class;
    }
    
    public void setFiles(List<RemoteFile> files) {
        this.files = files != null ? new ArrayList<>(files) : new ArrayList<>();
        fireTableDataChanged();
    }
    
    public void addFile(RemoteFile file) {
        files.add(file);
        fireTableRowsInserted(files.size() - 1, files.size() - 1);
    }
    
    public void removeFile(int index) {
        if (index >= 0 && index < files.size()) {
            files.remove(index);
            fireTableRowsDeleted(index, index);
        }
    }
    
    public RemoteFile getFileAt(int row) {
        if (row >= 0 && row < files.size()) {
            return files.get(row);
        }
        return null;
    }
    
    public List<RemoteFile> getFiles() {
        return new ArrayList<>(files);
    }
    
    public void clear() {
        int size = files.size();
        if (size > 0) {
            files.clear();
            fireTableRowsDeleted(0, size - 1);
        }
    }
}
