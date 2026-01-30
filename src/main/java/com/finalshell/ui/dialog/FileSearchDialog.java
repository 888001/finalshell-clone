package com.finalshell.ui.dialog;

import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    private SSHSession sshSession;
    private SwingWorker<Void, Object[]> searchWorker;
    
    private SearchCallback callback;
    
    public FileSearchDialog(Frame owner) {
        super(owner, "文件搜索", false);
        initUI();
    }

    public FileSearchDialog(Frame owner, SSHSession sshSession) {
        this(owner);
        this.sshSession = sshSession;
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
                        String fullPath = (String) tableModel.getValueAt(row, 1);
                        callback.onFileSelected(fullPath);
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

        if (sshSession == null || !sshSession.isConnected()) {
            JOptionPane.showMessageDialog(this, "请先建立SSH连接后再搜索", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        searching = true;
        searchButton.setEnabled(false);
        stopButton.setEnabled(true);
        tableModel.setRowCount(0);
        statusLabel.setText("搜索中...");

        final String basePath = pathField.getText().trim().isEmpty() ? "/" : pathField.getText().trim();
        final boolean recursive = recursiveCheck.isSelected();
        final boolean caseSensitive = caseSensitiveCheck.isSelected();

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

        searchWorker = new SwingWorker<Void, Object[]>() {
            @Override
            protected Void doInBackground() throws Exception {
                String nameFlag = caseSensitive ? "-name" : "-iname";
                String pattern = "*" + keyword + "*";

                StringBuilder cmd = new StringBuilder();
                cmd.append("find ");
                cmd.append(quoteForSh(basePath));
                if (!recursive) {
                    cmd.append(" -maxdepth 1");
                }
                cmd.append(" -type f ");
                cmd.append(nameFlag);
                cmd.append(" ");
                cmd.append(quoteForSh(pattern));
                cmd.append(" -printf ");
                cmd.append(quoteForSh("%p|%s|%T@\\n"));

                String output;
                try {
                    output = sshSession.exec(cmd.toString());
                } catch (SSHException e) {
                    throw e;
                }

                if (output == null || output.isEmpty()) {
                    return null;
                }

                String[] lines = output.split("\\r?\\n");
                for (String line : lines) {
                    if (!searching || isCancelled()) {
                        break;
                    }
                    if (line == null || line.isEmpty()) {
                        continue;
                    }

                    String[] parts = line.split("\\|", 3);
                    String fullPath = parts.length > 0 ? parts[0] : "";
                    String size = parts.length > 1 ? parts[1] : "";
                    String t = parts.length > 2 ? parts[2] : "";

                    String name = fullPath;
                    int idx = fullPath.lastIndexOf('/');
                    if (idx >= 0 && idx < fullPath.length() - 1) {
                        name = fullPath.substring(idx + 1);
                    }

                    String timeText = "";
                    if (t != null && !t.isEmpty()) {
                        try {
                            double seconds = Double.parseDouble(t);
                            long epochSecond = (long) seconds;
                            timeText = formatter.format(Instant.ofEpochSecond(epochSecond));
                        } catch (Exception ignored) {
                        }
                    }

                    publish(new Object[]{name, fullPath, size, timeText});
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Object[]> chunks) {
                for (Object[] row : chunks) {
                    tableModel.addRow(row);
                }
                statusLabel.setText("找到 " + tableModel.getRowCount() + " 个文件");
            }

            @Override
            protected void done() {
                searching = false;
                searchButton.setEnabled(true);
                stopButton.setEnabled(false);
                if (isCancelled()) {
                    statusLabel.setText("已停止，共找到 " + tableModel.getRowCount() + " 个文件");
                    return;
                }
                try {
                    get();
                    statusLabel.setText("搜索完成，共找到 " + tableModel.getRowCount() + " 个文件");
                } catch (Exception e) {
                    statusLabel.setText("搜索失败: " + e.getMessage());
                }
            }
        };

        searchWorker.execute();
    }
    
    private void stopSearch() {
        searching = false;
        if (searchWorker != null) {
            searchWorker.cancel(true);
        }
    }

    private static String quoteForSh(String value) {
        if (value == null) {
            return "''";
        }
        return "'" + value.replace("'", "'\\''") + "'";
    }
    
    public void setCallback(SearchCallback callback) {
        this.callback = callback;
    }
    
    public void setSearchPath(String path) {
        pathField.setText(path);
    }

    public void setSshSession(SSHSession sshSession) {
        this.sshSession = sshSession;
    }
    
    public interface SearchCallback {
        void onFileSelected(String filePath);
    }
}
