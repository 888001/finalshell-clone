package com.finalshell.ui.dialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * 文件搜索对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class FileSearchDialog extends JDialog {
    
    private JTextField pathField;
    private JTextField keywordField;
    private JCheckBox recursiveCheck;
    private JCheckBox caseSensitiveCheck;
    private JButton searchButton;
    private JButton stopButton;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private volatile boolean searching = false;
    
    private SearchCallback callback;
    
    public FileSearchDialog(Frame owner) {
        super(owner, "文件搜索", false);
        initUI();
    }
    
    private void initUI() {
        setSize(600, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(5, 5));
        
        // 搜索条件面板
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("路径:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        pathField = new JTextField("/");
        searchPanel.add(pathField, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        JButton browseButton = new JButton("...");
        browseButton.setPreferredSize(new Dimension(30, 25));
        searchPanel.add(browseButton, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        searchPanel.add(new JLabel("关键词:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        keywordField = new JTextField();
        searchPanel.add(keywordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        JPanel optionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        recursiveCheck = new JCheckBox("递归搜索", true);
        caseSensitiveCheck = new JCheckBox("区分大小写", false);
        optionPanel.add(recursiveCheck);
        optionPanel.add(caseSensitiveCheck);
        searchPanel.add(optionPanel, gbc);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // 结果表格
        String[] columns = {"文件名", "路径", "大小", "修改时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultTable.getSelectedRow();
                    if (row >= 0 && callback != null) {
                        String path = (String) tableModel.getValueAt(row, 1);
                        String name = (String) tableModel.getValueAt(row, 0);
                        callback.onFileSelected(path + "/" + name);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部面板
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        
        statusLabel = new JLabel("就绪");
        bottomPanel.add(statusLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchButton = new JButton("搜索");
        searchButton.addActionListener(e -> startSearch());
        buttonPanel.add(searchButton);
        
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopSearch());
        buttonPanel.add(stopButton);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        keywordField.addActionListener(e -> startSearch());
    }
    
    private void startSearch() {
        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入搜索关键词", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        searching = true;
        searchButton.setEnabled(false);
        stopButton.setEnabled(true);
        tableModel.setRowCount(0);
        statusLabel.setText("搜索中...");
        
        // 模拟搜索结果
        new Thread(() -> {
            try {
                for (int i = 0; i < 20 && searching; i++) {
                    final int index = i;
                    SwingUtilities.invokeLater(() -> {
                        tableModel.addRow(new Object[]{
                            keyword + "_" + index + ".txt",
                            pathField.getText(),
                            String.format("%.1f KB", Math.random() * 100),
                            "2026-01-21 15:00"
                        });
                        statusLabel.setText("找到 " + tableModel.getRowCount() + " 个文件");
                    });
                    Thread.sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    searching = false;
                    searchButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    statusLabel.setText("搜索完成，共找到 " + tableModel.getRowCount() + " 个文件");
                });
            }
        }).start();
    }
    
    private void stopSearch() {
        searching = false;
    }
    
    public void setCallback(SearchCallback callback) {
        this.callback = callback;
    }
    
    public void setSearchPath(String path) {
        pathField.setText(path);
    }
    
    public interface SearchCallback {
        void onFileSelected(String filePath);
    }
}
