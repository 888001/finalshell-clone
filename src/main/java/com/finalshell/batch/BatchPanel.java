package com.finalshell.batch;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量执行面板
 */
public class BatchPanel extends JPanel {
    private final ConfigManager configManager;
    private final BatchExecutor executor;
    
    private JList<ConnectConfig> connectionList;
    private DefaultListModel<ConnectConfig> connectionModel;
    private JTextArea commandArea;
    private JTable resultTable;
    private DefaultTableModel resultModel;
    private JTextArea outputArea;
    private JButton executeBtn;
    private JButton cancelBtn;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    
    private int completedTasks = 0;
    private int totalTasks = 0;
    
    public BatchPanel(ConfigManager configManager) {
        this.configManager = configManager;
        this.executor = new BatchExecutor();
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
        setupListeners();
        loadConnections();
    }
    
    private void initComponents() {
        // 左侧：连接选择
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        
        leftPanel.add(new JLabel("选择服务器:"), BorderLayout.NORTH);
        
        connectionModel = new DefaultListModel<>();
        connectionList = new JList<>(connectionModel);
        connectionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        connectionList.setCellRenderer(new ConnectionListRenderer());
        
        JScrollPane listScroll = new JScrollPane(connectionList);
        leftPanel.add(listScroll, BorderLayout.CENTER);
        
        JPanel listBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllBtn = new JButton("全选");
        JButton selectNoneBtn = new JButton("取消");
        selectAllBtn.addActionListener(e -> {
            connectionList.setSelectionInterval(0, connectionModel.size() - 1);
        });
        selectNoneBtn.addActionListener(e -> connectionList.clearSelection());
        listBtnPanel.add(selectAllBtn);
        listBtnPanel.add(selectNoneBtn);
        leftPanel.add(listBtnPanel, BorderLayout.SOUTH);
        
        add(leftPanel, BorderLayout.WEST);
        
        // 中间：命令输入和结果
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        
        // 命令输入区
        JPanel commandPanel = new JPanel(new BorderLayout(5, 5));
        commandPanel.setBorder(BorderFactory.createTitledBorder("命令"));
        
        commandArea = new JTextArea(4, 40);
        commandArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane cmdScroll = new JScrollPane(commandArea);
        commandPanel.add(cmdScroll, BorderLayout.CENTER);
        
        JPanel cmdBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        executeBtn = new JButton("执行");
        executeBtn.setBackground(new Color(46, 204, 113));
        executeBtn.setForeground(Color.WHITE);
        cancelBtn = new JButton("取消");
        cancelBtn.setEnabled(false);
        cmdBtnPanel.add(executeBtn);
        cmdBtnPanel.add(cancelBtn);
        commandPanel.add(cmdBtnPanel, BorderLayout.SOUTH);
        
        centerPanel.add(commandPanel, BorderLayout.NORTH);
        
        // 结果表格
        String[] columns = {"服务器", "状态", "耗时", "退出码"};
        resultModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(resultModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        resultTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        resultTable.setDefaultRenderer(Object.class, new StatusCellRenderer());
        
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(0, 200));
        
        // 输出详情
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("输出详情"));
        
        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane outScroll = new JScrollPane(outputArea);
        outputPanel.add(outScroll, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, outputPanel);
        splitPane.setResizeWeight(0.4);
        centerPanel.add(splitPane, BorderLayout.CENTER);
        
        // 进度条
        JPanel progressPanel = new JPanel(new BorderLayout(5, 5));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusLabel = new JLabel("就绪");
        progressPanel.add(progressBar, BorderLayout.CENTER);
        progressPanel.add(statusLabel, BorderLayout.EAST);
        centerPanel.add(progressPanel, BorderLayout.SOUTH);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void setupListeners() {
        executeBtn.addActionListener(e -> execute());
        cancelBtn.addActionListener(e -> cancel());
        
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showTaskOutput();
            }
        });
        
        executor.addListener(new BatchExecutor.BatchListener() {
            @Override
            public void onTaskAdded(BatchTask task) {
                SwingUtilities.invokeLater(() -> {
                    resultModel.addRow(new Object[]{
                        task.getHostDisplay(),
                        task.getStatus().getDisplayName(),
                        "-",
                        "-"
                    });
                });
            }
            
            @Override
            public void onTaskUpdate(BatchTask task) {
                SwingUtilities.invokeLater(() -> updateTaskRow(task));
            }
            
            @Override
            public void onBatchStart(int total) {
                SwingUtilities.invokeLater(() -> {
                    totalTasks = total;
                    completedTasks = 0;
                    progressBar.setValue(0);
                    statusLabel.setText("执行中: 0/" + total);
                    executeBtn.setEnabled(false);
                    cancelBtn.setEnabled(true);
                });
            }
            
            @Override
            public void onBatchComplete(int success, int failed) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setValue(100);
                    statusLabel.setText(String.format("完成: 成功 %d, 失败 %d", success, failed));
                    executeBtn.setEnabled(true);
                    cancelBtn.setEnabled(false);
                    
                    JOptionPane.showMessageDialog(BatchPanel.this,
                        String.format("批量执行完成\n成功: %d\n失败: %d", success, failed),
                        "执行完成",
                        success > 0 && failed == 0 ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
                });
            }
        });
    }
    
    private void loadConnections() {
        connectionModel.clear();
        for (ConnectConfig config : configManager.getConnections()) {
            if ("SSH".equals(config.getType())) {
                connectionModel.addElement(config);
            }
        }
    }
    
    private void execute() {
        List<ConnectConfig> selected = connectionList.getSelectedValuesList();
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请选择至少一个服务器", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String command = commandArea.getText().trim();
        if (command.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入要执行的命令", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 清空之前的结果
        resultModel.setRowCount(0);
        outputArea.setText("");
        executor.clear();
        
        // 添加任务
        executor.addTasks(selected, command);
        
        // 异步执行
        executor.executeAllAsync(null);
    }
    
    private void cancel() {
        executor.cancel();
        statusLabel.setText("已取消");
    }
    
    private void updateTaskRow(BatchTask task) {
        List<BatchTask> tasks = executor.getTasks();
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId().equals(task.getId())) {
                index = i;
                break;
            }
        }
        
        if (index >= 0 && index < resultModel.getRowCount()) {
            resultModel.setValueAt(task.getStatus().getDisplayName(), index, 1);
            resultModel.setValueAt(formatDuration(task.getDuration()), index, 2);
            resultModel.setValueAt(task.getExitCode(), index, 3);
            
            if (task.getStatus() != BatchTask.BatchTaskStatus.PENDING && 
                task.getStatus() != BatchTask.BatchTaskStatus.RUNNING) {
                completedTasks++;
                int progress = (int) ((completedTasks * 100.0) / totalTasks);
                progressBar.setValue(progress);
                statusLabel.setText("执行中: " + completedTasks + "/" + totalTasks);
            }
        }
    }
    
    private void showTaskOutput() {
        int row = resultTable.getSelectedRow();
        if (row < 0) return;
        
        List<BatchTask> tasks = executor.getTasks();
        if (row < tasks.size()) {
            BatchTask task = tasks.get(row);
            StringBuilder sb = new StringBuilder();
            sb.append("=== ").append(task.getHostDisplay()).append(" ===\n");
            sb.append("命令: ").append(task.getCommand()).append("\n");
            sb.append("状态: ").append(task.getStatus().getDisplayName()).append("\n");
            sb.append("退出码: ").append(task.getExitCode()).append("\n");
            sb.append("\n--- 标准输出 ---\n");
            sb.append(task.getOutput());
            if (!task.getError().isEmpty()) {
                sb.append("\n--- 标准错误 ---\n");
                sb.append(task.getError());
            }
            outputArea.setText(sb.toString());
            outputArea.setCaretPosition(0);
        }
    }
    
    private String formatDuration(long ms) {
        if (ms < 1000) return ms + "ms";
        if (ms < 60000) return String.format("%.1fs", ms / 1000.0);
        return String.format("%dm%ds", ms / 60000, (ms % 60000) / 1000);
    }
    
    /**
     * 刷新连接列表
     */
    public void refresh() {
        loadConnections();
    }
    
    /**
     * 连接列表渲染器
     */
    private static class ConnectionListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ConnectConfig) {
                ConnectConfig config = (ConnectConfig) value;
                setText(config.getName() + " (" + config.getHost() + ")");
            }
            return this;
        }
    }
    
    /**
     * 状态单元格渲染器
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 1 && value != null) {
                String status = value.toString();
                if ("成功".equals(status)) {
                    setForeground(new Color(46, 204, 113));
                } else if ("失败".equals(status) || "超时".equals(status)) {
                    setForeground(new Color(231, 76, 60));
                } else if ("执行中".equals(status)) {
                    setForeground(new Color(52, 152, 219));
                } else if ("已取消".equals(status)) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(table.getForeground());
                }
            } else {
                setForeground(table.getForeground());
            }
            
            return this;
        }
    }
}
