package com.finalshell.ui.table;

import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 连接列表表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class ConnListTable extends JTable {
    
    private int hoverRow = -1;
    private ConnListTableModel model;
    
    public ConnListTable() {
        model = new ConnListTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setTableHeader(null);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setRowHeight(50);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new ConnListRenderer());
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoverRow) {
                    hoverRow = row;
                    repaint();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                repaint();
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = getSelectedRow();
                    if (row >= 0) {
                        ConnectConfig config = model.getConfigAt(row);
                        if (config != null) {
                            firePropertyChange("openConnection", null, config);
                        }
                    }
                }
            }
        });
    }
    
    public int getHoverRow() {
        return hoverRow;
    }
    
    public void setConnections(java.util.List<ConnectConfig> configs) {
        model.setConnections(configs);
    }
    
    public ConnectConfig getSelectedConfig() {
        int row = getSelectedRow();
        return row >= 0 ? model.getConfigAt(row) : null;
    }
    
    public ConnListTableModel getConnListModel() {
        return model;
    }
}
