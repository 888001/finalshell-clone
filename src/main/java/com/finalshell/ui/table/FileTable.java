package com.finalshell.ui.table;

import com.finalshell.sftp.RemoteFile;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 文件列表表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class FileTable extends JTable {
    
    private FileTableModel model;
    
    public FileTable() {
        model = new FileTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new FileCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 5) {
            columnModel.getColumn(0).setPreferredWidth(200); // 名称
            columnModel.getColumn(1).setPreferredWidth(80);  // 大小
            columnModel.getColumn(2).setPreferredWidth(80);  // 类型
            columnModel.getColumn(3).setPreferredWidth(120); // 修改时间
            columnModel.getColumn(4).setPreferredWidth(80);  // 权限
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        RemoteFile file = model.getFileAt(row);
                        if (file != null) {
                            firePropertyChange("openFile", null, file);
                        }
                    }
                }
            }
        });
    }
    
    public void setFiles(List<RemoteFile> files) {
        model.setFiles(files);
    }
    
    public RemoteFile getSelectedFile() {
        int row = getSelectedRow();
        return row >= 0 ? model.getFileAt(row) : null;
    }
    
    public List<RemoteFile> getSelectedFiles() {
        int[] rows = getSelectedRows();
        java.util.List<RemoteFile> files = new java.util.ArrayList<>();
        for (int row : rows) {
            RemoteFile file = model.getFileAt(row);
            if (file != null) {
                files.add(file);
            }
        }
        return files;
    }
    
    public FileTableModel getFileModel() {
        return model;
    }
}
