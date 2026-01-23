package com.finalshell.process;

import com.finalshell.ssh.SSHSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * 进程管理面板
 */
public class ProcessPanel extends JPanel {
    private final SSHSession session;
    private ProcessManager processManager;
    
    private JTable processTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField searchField;
    private JTextArea detailArea;
    private JButton refreshBtn;
    private JButton killBtn;
    private JButton forceKillBtn;
    private JLabel statusLabel;
    private Timer autoRefreshTimer;
    
    public ProcessPanel(SSHSession session) {
        this.session = session;
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        initComponents();
        setupListeners();
        
        if (session.isConnected()) {
            initProcessManager();
        }
    }
    
    private void initComponents() {
        // 工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        refreshBtn = new JButton("刷新");
        killBtn = new JButton("终止 (SIGTERM)");
        forceKillBtn = new JButton("强制终止 (SIGKILL)");
        killBtn.setEnabled(false);
        forceKillBtn.setEnabled(false);
        
        JCheckBox autoRefreshCheck = new JCheckBox("自动刷新", false);
        JSpinner intervalSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 60, 1));
        intervalSpinner.setPreferredSize(new Dimension(50, 25));
        
        searchField = new JTextField(20);
        searchField.setToolTipText("搜索进程 (PID/用户/命令)");
        
        toolBar.add(refreshBtn);
        toolBar.addSeparator();
        toolBar.add(killBtn);
        toolBar.add(forceKillBtn);
        toolBar.addSeparator();
        toolBar.add(autoRefreshCheck);
        toolBar.add(new JLabel(" 间隔: "));
        toolBar.add(intervalSpinner);
        toolBar.add(new JLabel(" 秒"));
        toolBar.addSeparator();
        toolBar.add(new JLabel(" 搜索: "));
        toolBar.add(searchField);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 进程表格
        String[] columns = {"PID", "用户", "CPU%", "内存%", "内存", "状态", "命令"};
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
        processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        processTable.setAutoCreateRowSorter(false);
        
        sorter = new TableRowSorter<>(tableModel);
        processTable.setRowSorter(sorter);
        
        // 设置列宽
        processTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        processTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        processTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        processTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        processTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        processTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        processTable.getColumnModel().getColumn(6).setPreferredWidth(400);
        
        // CPU/内存使用率着色
        processTable.setDefaultRenderer(Double.class, new UsageRenderer());
        
        JScrollPane tableScroll = new JScrollPane(processTable);
        
        // 详情面板
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createTitledBorder("进程详情"));
        detailPanel.setPreferredSize(new Dimension(0, 150));
        
        detailArea = new JTextArea();
        detailArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        detailArea.setEditable(false);
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailPanel.add(detailScroll, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, detailPanel);
        splitPane.setResizeWeight(0.7);
        add(splitPane, BorderLayout.CENTER);
        
        // 状态栏
        statusLabel = new JLabel("就绪");
        add(statusLabel, BorderLayout.SOUTH);
        
        // 自动刷新定时器
        autoRefreshTimer = new Timer(5000, e -> refresh());
        
        autoRefreshCheck.addActionListener(e -> {
            if (autoRefreshCheck.isSelected()) {
                int interval = (Integer) intervalSpinner.getValue() * 1000;
                autoRefreshTimer.setDelay(interval);
                autoRefreshTimer.start();
            } else {
                autoRefreshTimer.stop();
            }
        });
        
        intervalSpinner.addChangeListener(e -> {
            int interval = (Integer) intervalSpinner.getValue() * 1000;
            autoRefreshTimer.setDelay(interval);
        });
    }
    
    private void setupListeners() {
        refreshBtn.addActionListener(e -> refresh());
        
        killBtn.addActionListener(e -> killProcess(15));
        forceKillBtn.addActionListener(e -> killProcess(9));
        
        processTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean selected = processTable.getSelectedRow() >= 0;
                killBtn.setEnabled(selected);
                forceKillBtn.setEnabled(selected);
                
                if (selected) {
                    showProcessDetail();
                }
            }
        });
        
        processTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showProcessDetail();
                }
            }
        });
        
        searchField.addActionListener(e -> filterProcesses());
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProcesses(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProcesses(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProcesses(); }
        });
    }
    
    private void initProcessManager() {
        processManager = new ProcessManager(session);
        refresh();
    }
    
    public void refresh() {
        if (processManager == null) return;
        
        statusLabel.setText("正在刷新...");
        refreshBtn.setEnabled(false);
        
        processManager.getProcessListAsync(new ProcessManager.ProcessListCallback() {
            @Override
            public void onSuccess(List<ProcessInfo> processes) {
                SwingUtilities.invokeLater(() -> {
                    updateTable(processes);
                    statusLabel.setText("共 " + processes.size() + " 个进程");
                    refreshBtn.setEnabled(true);
                });
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("错误: " + error);
                    refreshBtn.setEnabled(true);
                });
            }
        });
    }
    
    private void updateTable(List<ProcessInfo> processes) {
        tableModel.setRowCount(0);
        
        for (ProcessInfo p : processes) {
            tableModel.addRow(new Object[]{
                p.getPid(),
                p.getUser(),
                p.getCpuPercent(),
                p.getMemPercent(),
                p.getMemoryDisplay(),
                p.getStat(),
                p.getCommand()
            });
        }
    }
    
    private void filterProcesses() {
        String text = searchField.getText().trim().toLowerCase();
        if (text.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }
    
    private void killProcess(int signal) {
        int row = processTable.getSelectedRow();
        if (row < 0) return;
        
        int modelRow = processTable.convertRowIndexToModel(row);
        int pid = (Integer) tableModel.getValueAt(modelRow, 0);
        String command = (String) tableModel.getValueAt(modelRow, 6);
        
        String signalName = signal == 9 ? "SIGKILL (强制终止)" : "SIGTERM (正常终止)";
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("确定要向进程 %d 发送 %s 信号?\n命令: %s", pid, signalName, command),
            "确认终止进程",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        new Thread(() -> {
            try {
                boolean success = processManager.killProcess(pid, signal);
                SwingUtilities.invokeLater(() -> {
                    if (success) {
                        JOptionPane.showMessageDialog(this, "信号已发送", "成功", JOptionPane.INFORMATION_MESSAGE);
                        refresh();
                    } else {
                        JOptionPane.showMessageDialog(this, "发送信号失败", "失败", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "错误: " + e.getMessage(), "失败", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    
    private void showProcessDetail() {
        int row = processTable.getSelectedRow();
        if (row < 0) return;
        
        int modelRow = processTable.convertRowIndexToModel(row);
        int pid = (Integer) tableModel.getValueAt(modelRow, 0);
        
        detailArea.setText("正在加载进程详情...");
        
        new Thread(() -> {
            try {
                String detail = processManager.getProcessDetail(pid);
                SwingUtilities.invokeLater(() -> {
                    detailArea.setText(detail);
                    detailArea.setCaretPosition(0);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    detailArea.setText("获取详情失败: " + e.getMessage());
                });
            }
        }).start();
    }
    
    public void cleanup() {
        if (autoRefreshTimer != null) {
            autoRefreshTimer.stop();
        }
        if (processManager != null) {
            processManager.shutdown();
        }
    }
    
    /**
     * 使用率渲染器
     */
    private static class UsageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof Double) {
                double val = (Double) value;
                setText(String.format("%.1f", val));
                
                if (!isSelected) {
                    if (val > 80) {
                        setBackground(new Color(255, 200, 200));
                    } else if (val > 50) {
                        setBackground(new Color(255, 240, 200));
                    } else {
                        setBackground(table.getBackground());
                    }
                }
            }
            
            setHorizontalAlignment(SwingConstants.RIGHT);
            return this;
        }
    }
}
