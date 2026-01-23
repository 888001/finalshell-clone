package com.finalshell.ui.table;

import com.finalshell.hotkey.HotkeyConfig;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 快捷键配置表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class HotkeyTable extends JTable {
    
    private HotkeyTableModel model;
    
    public HotkeyTable() {
        model = new HotkeyTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(28);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new HotkeyCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 3) {
            columnModel.getColumn(0).setPreferredWidth(150); // 功能
            columnModel.getColumn(1).setPreferredWidth(150); // 快捷键
            columnModel.getColumn(2).setPreferredWidth(100); // 操作
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        HotkeyConfig config = model.getHotkeyAt(row);
                        if (config != null) {
                            firePropertyChange("editHotkey", null, config);
                        }
                    }
                }
            }
        });
    }
    
    public void setHotkeys(List<HotkeyConfig> hotkeys) {
        model.setHotkeys(hotkeys);
    }
    
    public HotkeyConfig getSelectedHotkey() {
        int row = getSelectedRow();
        return row >= 0 ? model.getHotkeyAt(row) : null;
    }
    
    public HotkeyTableModel getHotkeyModel() {
        return model;
    }
}
