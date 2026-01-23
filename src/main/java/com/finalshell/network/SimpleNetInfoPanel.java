package com.finalshell.network;

import javax.swing.*;
import java.awt.*;

/**
 * 简单网络信息面板
 * 显示网络连接信息
 */
public class SimpleNetInfoPanel extends JPanel {
    
    private SocketTable socketTable;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    public SimpleNetInfoPanel() {
        setLayout(new BorderLayout());
        initUI();
    }
    
    private void initUI() {
        socketTable = new SocketTable();
        scrollPane = new JScrollPane(socketTable);
        add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void updateData(java.util.List<SocketRow> rows) {
        socketTable.setData(rows);
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public SocketTable getSocketTable() {
        return socketTable;
    }
}
