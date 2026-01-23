package com.finalshell.portmap;

import javax.swing.*;
import javax.swing.table.*;

/**
 * 端口映射规则表格
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class MapRuleListTable extends JTable {
    
    public MapRuleListTable() {
        this(new MapRuleListModel());
    }
    
    public MapRuleListTable(MapRuleListModel model) {
        super(model);
        initUI();
    }
    
    private void initUI() {
        setRowHeight(25);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        
        TableColumnModel columnModel = getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100);
        columnModel.getColumn(1).setPreferredWidth(60);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(60);
        columnModel.getColumn(4).setPreferredWidth(100);
        columnModel.getColumn(5).setPreferredWidth(60);
        columnModel.getColumn(6).setPreferredWidth(60);
        
        setDefaultRenderer(Object.class, new MapRuleRenderer());
    }
    
    public MapRule getSelectedRule() {
        int row = getSelectedRow();
        if (row >= 0) {
            return ((MapRuleListModel) getModel()).getRuleAt(row);
        }
        return null;
    }
}
