package com.finalshell.ui.table;

import com.finalshell.monitor.DiskInfo;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * 磁盘使用率表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class DFTable extends JTable {
    
    private DFTableModel model;
    
    public DFTable() {
        model = new DFTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(24);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new DFCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 6) {
            columnModel.getColumn(0).setPreferredWidth(100); // 文件系统
            columnModel.getColumn(1).setPreferredWidth(80);  // 总大小
            columnModel.getColumn(2).setPreferredWidth(80);  // 已用
            columnModel.getColumn(3).setPreferredWidth(80);  // 可用
            columnModel.getColumn(4).setPreferredWidth(60);  // 使用率
            columnModel.getColumn(5).setPreferredWidth(120); // 挂载点
        }
    }
    
    public void setDiskInfos(List<DiskInfo> infos) {
        model.setDiskInfos(infos);
    }
    
    public DiskInfo getSelectedDiskInfo() {
        int row = getSelectedRow();
        return row >= 0 ? model.getDiskInfoAt(row) : null;
    }
    
    public DFTableModel getDFModel() {
        return model;
    }
}
