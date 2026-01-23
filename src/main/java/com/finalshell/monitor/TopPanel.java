package com.finalshell.monitor;

import javax.swing.*;
import java.awt.*;

/**
 * 进程TOP面板
 * 显示进程列表和详细信息
 */
public class TopPanel extends JPanel {
    
    private TopTable topTable;
    private JScrollPane scrollPane;
    private JLabel statusLabel;
    
    public TopPanel() {
        setLayout(new BorderLayout());
        initUI();
    }
    
    private void initUI() {
        topTable = new TopTable();
        scrollPane = new JScrollPane(topTable);
        add(scrollPane, BorderLayout.CENTER);
        
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void updateData(java.util.List<TopRow> rows) {
        topTable.setData(rows);
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public TopTable getTopTable() {
        return topTable;
    }
}
