package com.finalshell.ui.table;

import com.finalshell.config.PortForwardConfig;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 端口转发配置表格
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Table_Model_Renderer_Listener_Analysis.md
 */
public class PortForwardTable extends JTable {
    
    private PortForwardTableModel model;
    
    public PortForwardTable() {
        model = new PortForwardTableModel();
        setModel(model);
        init();
    }
    
    private void init() {
        setRowHeight(26);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        setDefaultRenderer(Object.class, new PortForwardCellRenderer());
        
        getTableHeader().setReorderingAllowed(false);
        
        TableColumnModel columnModel = getColumnModel();
        if (columnModel.getColumnCount() >= 5) {
            columnModel.getColumn(0).setPreferredWidth(60);  // 类型
            columnModel.getColumn(1).setPreferredWidth(80);  // 本地端口
            columnModel.getColumn(2).setPreferredWidth(120); // 远程主机
            columnModel.getColumn(3).setPreferredWidth(80);  // 远程端口
            columnModel.getColumn(4).setPreferredWidth(60);  // 状态
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        PortForwardConfig config = model.getConfigAt(row);
                        if (config != null) {
                            firePropertyChange("editPortForward", null, config);
                        }
                    }
                }
            }
        });
    }
    
    public void setConfigs(List<PortForwardConfig> configs) {
        model.setConfigs(configs);
    }
    
    public void addConfig(PortForwardConfig config) {
        model.addConfig(config);
    }
    
    public void removeSelectedConfig() {
        int row = getSelectedRow();
        if (row >= 0) {
            model.removeConfig(row);
        }
    }
    
    public PortForwardConfig getSelectedConfig() {
        int row = getSelectedRow();
        return row >= 0 ? model.getConfigAt(row) : null;
    }
    
    public PortForwardTableModel getPortForwardModel() {
        return model;
    }
}
