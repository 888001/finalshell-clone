package com.finalshell.network;

import com.finalshell.ui.table.NetTable;

import javax.swing.*;
import java.awt.*;

/**
 * 网络监控面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetPanel extends JPanel {
    
    private NetTable netTable;
    private NetDetailPanel detailPanel;
    private JButton refreshButton;
    private JButton closeButton;
    
    public NetPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        netTable = new NetTable();
        JScrollPane scrollPane = new JScrollPane(netTable);
        
        detailPanel = new NetDetailPanel();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, detailPanel);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.7);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("刷新");
        closeButton = new JButton("关闭");
        
        refreshButton.addActionListener(e -> refresh());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        
        add(splitPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        netTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                NetRow row = netTable.getSelectedNetRow();
                if (row != null) {
                    detailPanel.showDetail(row);
                }
            }
        });
    }
    
    public void refresh() {
        netTable.refresh();
    }
    
    public NetTable getNetTable() {
        return netTable;
    }
    
    public void addCloseListener(java.awt.event.ActionListener listener) {
        closeButton.addActionListener(listener);
    }
}
