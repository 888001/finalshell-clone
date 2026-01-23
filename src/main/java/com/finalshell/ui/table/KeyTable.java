package com.finalshell.ui.table;

import com.finalshell.key.SecretKey;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * SSH密钥管理表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class KeyTable extends JTable {
    
    private KeyTableModel model;
    
    public KeyTable() {
        model = new KeyTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(26);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new KeyRenderer());
        setDefaultRenderer(Boolean.class, new KeyRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 4) {
            columnModel.getColumn(0).setPreferredWidth(30);  // 选择
            columnModel.getColumn(1).setPreferredWidth(150); // 名称
            columnModel.getColumn(2).setPreferredWidth(80);  // 类型
            columnModel.getColumn(3).setPreferredWidth(80);  // 长度
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                int col = columnAtPoint(e.getPoint());
                
                if (col == 0 && row >= 0) {
                    model.setValueAt(true, row, 0);
                }
                
                if (e.getClickCount() == 2 && row >= 0) {
                    SecretKey key = model.getKeyAt(row);
                    if (key != null) {
                        firePropertyChange("editKey", null, key);
                    }
                }
            }
        });
    }
    
    public void setKeys(List<SecretKey> keys) {
        model.setKeys(keys);
    }
    
    public void addKey(SecretKey key) {
        model.addKey(key);
    }
    
    public void removeSelectedKey() {
        int row = getSelectedRow();
        if (row >= 0) {
            model.removeKey(row);
        }
    }
    
    public SecretKey getSelectedKey() {
        int row = getSelectedRow();
        return row >= 0 ? model.getKeyAt(row) : null;
    }
    
    public void setCurrentKey(String keyId) {
        model.setCurrentKeyId(keyId);
    }
    
    public KeyTableModel getKeyModel() {
        return model;
    }
}
