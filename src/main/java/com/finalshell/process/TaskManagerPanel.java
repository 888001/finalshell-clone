package com.finalshell.process;

import com.finalshell.ssh.SSHSession;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 任务管理器面板 - 进程管理增强版
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SysInfo_Task_UI_DeepAnalysis.md
 */
public class TaskManagerPanel extends JPanel {
    
    private final SSHSession session;
    private final ProcessManager processManager;
    
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortCombo;
    private JLabel statusLabel;
    private JProgressBar cpuBar;
    private JProgressBar memBar;
    
    private ScheduledExecutorService scheduler;
    private volatile boolean autoRefresh = true;
    
    public TaskManagerPanel(SSHSession session) {
        this.session = session;
        this.processManager = new ProcessManager(session);
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initComponents();
        startAutoRefresh();
    }
    
    private void initComponents() {
        // 顶部工具栏
        JPanel toolBar = new JPanel(new BorderLayout(5, 0));
        
        // 搜索
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("搜索:"), BorderLayout.WEST);
        searchField = new JTextField(15);
        searchField.addActionListener(e -> filterProcesses());
        searchPanel.add(searchField, BorderLayout.CENTER);
        toolBar.add(searchPanel, BorderLayout.WEST);
        
        // 排序
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        sortPanel.add(new JLabel("排序:"));
        sortCombo = new JComboBox<>(new String[]{"CPU%", "内存%", "PID", "名称"});
        sortCombo.addActionListener(e -> sortProcesses());
        sortPanel.add(sortCombo);
        toolBar.add(sortPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        
        JButton refreshBtn = new JButton("刷新");
        refreshBtn.addActionListener(e -> refreshProcesses());
        btnPanel.add(refreshBtn);
        
        JButton killBtn = new JButton("结束进程");
        killBtn.addActionListener(e -> killSelectedProcess());
        btnPanel.add(killBtn);
        
        JCheckBox autoBox = new JCheckBox("自动刷新", true);
        autoBox.addActionListener(e -> autoRefresh = autoBox.isSelected());
        btnPanel.add(autoBox);
        
        toolBar.add(btnPanel, BorderLayout.EAST);
        add(toolBar, BorderLayout.NORTH);
        
        // 进程表格
        String[] columns = {"PID", "用户", "CPU%", "内存%", "VSZ", "RSS", "状态", "启动时间", "命令"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Integer.class;
                if (column == 2 || column == 3) return Double.class;
                return String.class;
            }
        };
        
        processTable = new JTable(tableModel);
        processTable.setAutoCreateRowSorter(true);
        processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processTable.setRowHeight(20);
        
        // 设置列宽
        processTable.getColumnModel().getColumn(0).setPreferredWidth(60);   // PID
        processTable.getColumnModel().getColumn(1).setPreferredWidth(80);   // 用户
        processTable.getColumnModel().getColumn(2).setPreferredWidth(60);   // CPU%
        processTable.getColumnModel().getColumn(3).setPreferredWidth(60);   // 内存%
        processTable.getColumnModel().getColumn(4).setPreferredWidth(80);   // VSZ
        processTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // RSS
        processTable.getColumnModel().getColumn(6).setPreferredWidth(50);   // 状态
        processTable.getColumnModel().getColumn(7).setPreferredWidth(100);  // 启动时间
        processTable.getColumnModel().getColumn(8).setPreferredWidth(300);  // 命令
        
        // CPU和内存列使用进度条渲染
        processTable.getColumnModel().getColumn(2).setCellRenderer(new ProgressCellRenderer());
        processTable.getColumnModel().getColumn(3).setCellRenderer(new ProgressCellRenderer());
        
        // 双击查看详情
        processTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showProcessDetails();
                }
            }
        });
        
        // 右键菜单
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem killItem = new JMenuItem("结束进程");
        killItem.addActionListener(e -> killSelectedProcess());
        popupMenu.add(killItem);
        
        JMenuItem detailItem = new JMenuItem("查看详情");
        detailItem.addActionListener(e -> showProcessDetails());
        popupMenu.add(detailItem);
        
        processTable.setComponentPopupMenu(popupMenu);
        
        JScrollPane scrollPane = new JScrollPane(processTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // 底部状态栏
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // CPU和内存进度条
        JPanel resourcePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        JPanel cpuPanel = new JPanel(new BorderLayout(5, 0));
        cpuPanel.add(new JLabel("CPU:"), BorderLayout.WEST);
        cpuBar = new JProgressBar(0, 100);
        cpuBar.setStringPainted(true);
        cpuPanel.add(cpuBar, BorderLayout.CENTER);
        resourcePanel.add(cpuPanel);
        
        JPanel memPanel = new JPanel(new BorderLayout(5, 0));
        memPanel.add(new JLabel("内存:"), BorderLayout.WEST);
        memBar = new JProgressBar(0, 100);
        memBar.setStringPainted(true);
        memPanel.add(memBar, BorderLayout.CENTER);
        resourcePanel.add(memPanel);
        
        statusPanel.add(resourcePanel, BorderLayout.CENTER);
        
        statusLabel = new JLabel("就绪");
        statusPanel.add(statusLabel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private void startAutoRefresh() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            if (autoRefresh && session != null && session.isConnected()) {
                SwingUtilities.invokeLater(this::refreshProcesses);
            }
        }, 0, 3, TimeUnit.SECONDS);
    }
    
    private void refreshProcesses() {
        if (session == null || !session.isConnected()) {
            statusLabel.setText("未连接");
            return;
        }
        
        statusLabel.setText("刷新中...");
        
        new SwingWorker<List<ProcessInfo>, Void>() {
            @Override
            protected List<ProcessInfo> doInBackground() throws Exception {
                return processManager.getProcessList();
            }
            
            @Override
            protected void done() {
                try {
                    List<ProcessInfo> processes = get();
                    updateTable(processes);
                    statusLabel.setText("进程数: " + processes.size());
                    
                    // 更新资源使用
                    double totalCpu = 0;
                    double totalMem = 0;
                    for (ProcessInfo p : processes) {
                        totalCpu += p.getCpuPercent();
                        totalMem += p.getMemPercent();
                    }
                    cpuBar.setValue((int) Math.min(100, totalCpu));
                    cpuBar.setString(String.format("%.1f%%", totalCpu));
                    memBar.setValue((int) Math.min(100, totalMem));
                    memBar.setString(String.format("%.1f%%", totalMem));
                    
                } catch (Exception e) {
                    statusLabel.setText("错误: " + e.getMessage());
                }
            }
        }.execute();
    }
    
    private void updateTable(List<ProcessInfo> processes) {
        tableModel.setRowCount(0);
        
        String filter = searchField.getText().toLowerCase().trim();
        
        for (ProcessInfo p : processes) {
            if (!filter.isEmpty()) {
                String searchStr = (p.getCommand() + p.getUser()).toLowerCase();
                if (!searchStr.contains(filter)) continue;
            }
            
            tableModel.addRow(new Object[]{
                p.getPid(),
                p.getUser(),
                p.getCpuPercent(),
                p.getMemPercent(),
                formatSize(p.getVsz() * 1024),
                formatSize(p.getRss() * 1024),
                p.getState(),
                p.getStartTime(),
                p.getCommand()
            });
        }
    }
    
    private void filterProcesses() {
        refreshProcesses();
    }
    
    private void sortProcesses() {
        int col = sortCombo.getSelectedIndex() + 2;
        if (col == 2) col = 2;      // CPU
        else if (col == 3) col = 3;  // 内存
        else if (col == 4) col = 0;  // PID
        else if (col == 5) col = 8;  // 名称
        
        TableRowSorter<?> sorter = (TableRowSorter<?>) processTable.getRowSorter();
        if (sorter != null) {
            sorter.setSortKeys(Collections.singletonList(
                new RowSorter.SortKey(col, SortOrder.DESCENDING)));
        }
    }
    
    private void killSelectedProcess() {
        int row = processTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要结束的进程", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = processTable.convertRowIndexToModel(row);
        int pid = (Integer) tableModel.getValueAt(modelRow, 0);
        String cmd = (String) tableModel.getValueAt(modelRow, 8);
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定要结束进程吗?\n\nPID: " + pid + "\n命令: " + cmd,
            "确认",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            boolean success = processManager.killProcess(pid, false);
            if (success) {
                JOptionPane.showMessageDialog(this, "进程已结束", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshProcesses();
            } else {
                JOptionPane.showMessageDialog(this, "结束进程失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showProcessDetails() {
        int row = processTable.getSelectedRow();
        if (row < 0) return;
        
        int modelRow = processTable.convertRowIndexToModel(row);
        StringBuilder details = new StringBuilder();
        
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            details.append(tableModel.getColumnName(i)).append(": ");
            details.append(tableModel.getValueAt(modelRow, i)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JOptionPane.showMessageDialog(this, 
            new JScrollPane(textArea), 
            "进程详情", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f K", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f M", bytes / (1024.0 * 1024));
        return String.format("%.1f G", bytes / (1024.0 * 1024 * 1024));
    }
    
    public void cleanup() {
        autoRefresh = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
    
    /**
     * 进度条单元格渲染器
     */
    private class ProgressCellRenderer extends DefaultTableCellRenderer {
        private final JProgressBar progressBar = new JProgressBar(0, 100);
        
        public ProgressCellRenderer() {
            progressBar.setStringPainted(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (value instanceof Double) {
                double percent = (Double) value;
                progressBar.setValue((int) percent);
                progressBar.setString(String.format("%.1f%%", percent));
                
                // 颜色
                if (percent > 80) {
                    progressBar.setForeground(new Color(255, 80, 80));
                } else if (percent > 50) {
                    progressBar.setForeground(new Color(255, 180, 80));
                } else {
                    progressBar.setForeground(new Color(80, 180, 80));
                }
                
                return progressBar;
            }
            
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
