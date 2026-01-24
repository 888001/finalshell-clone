package com.finalshell.monitor;

import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Monitor Panel - System monitoring dashboard
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: UI_Parameters_Reference.md - Monitor Panel
 */
public class MonitorPanel extends JPanel implements MonitorSession.MonitorListener {
    
    private static final Logger logger = LoggerFactory.getLogger(MonitorPanel.class);
    
    private SSHSession sshSession;
    private MonitorSession monitorSession;
    
    // Overview panel
    private JLabel hostLabel;
    private JLabel osLabel;
    private JLabel uptimeLabel;
    private JLabel loadLabel;
    
    // CPU panel
    private JProgressBar cpuBar;
    private JLabel cpuLabel;
    private UsageChart cpuChart;
    
    // Memory panel
    private JProgressBar memBar;
    private JLabel memLabel;
    private UsageChart memChart;
    
    // Disk panel
    private DefaultTableModel diskModel;
    private JTable diskTable;
    
    // Network panel
    private JLabel netRxLabel;
    private JLabel netTxLabel;
    private UsageChart netChart;
    
    // Process panel
    private DefaultTableModel processModel;
    private JTable processTable;
    
    // Status
    private JLabel statusLabel;
    private JToggleButton startStopBtn;
    
    public MonitorPanel() {
        initComponents();
        initLayout();
    }
    
    public MonitorPanel(SSHSession sshSession) {
        this.sshSession = sshSession;
        
        initComponents();
        initLayout();
    }
    
    public void refresh() {
        if (monitorSession != null) {
            monitorSession.refresh();
        }
    }
    
    public void updateData(MonitorData data) {
        updateUI(data);
    }
    
    private void initComponents() {
        // Overview
        hostLabel = new JLabel("-");
        osLabel = new JLabel("-");
        uptimeLabel = new JLabel("-");
        loadLabel = new JLabel("-");
        
        // CPU
        cpuBar = new JProgressBar(0, 100);
        cpuBar.setStringPainted(true);
        cpuLabel = new JLabel("CPU: -");
        cpuChart = new UsageChart("CPU", Color.BLUE);
        
        // Memory
        memBar = new JProgressBar(0, 100);
        memBar.setStringPainted(true);
        memLabel = new JLabel("内存: -");
        memChart = new UsageChart("内存", Color.GREEN);
        
        // Disk table
        String[] diskCols = {"挂载点", "总计", "已用", "可用", "使用率"};
        diskModel = new DefaultTableModel(diskCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        diskTable = new JTable(diskModel);
        diskTable.setRowHeight(22);
        
        // Network
        netRxLabel = new JLabel("下载: -");
        netTxLabel = new JLabel("上传: -");
        netChart = new UsageChart("网络", Color.ORANGE);
        
        // Process table
        String[] procCols = {"PID", "用户", "CPU%", "内存%", "命令"};
        processModel = new DefaultTableModel(procCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        processTable = new JTable(processModel);
        processTable.setRowHeight(20);
        
        // Status
        statusLabel = new JLabel("未启动");
        startStopBtn = new JToggleButton("启动监控");
        startStopBtn.addActionListener(e -> {
            if (startStopBtn.isSelected()) {
                startMonitoring();
            } else {
                stopMonitoring();
            }
        });
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Top panel - overview + controls
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel overviewPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        overviewPanel.setBorder(BorderFactory.createTitledBorder("系统概览"));
        overviewPanel.add(createLabelPanel("主机", hostLabel));
        overviewPanel.add(createLabelPanel("系统", osLabel));
        overviewPanel.add(createLabelPanel("运行时间", uptimeLabel));
        overviewPanel.add(createLabelPanel("负载", loadLabel));
        topPanel.add(overviewPanel, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(statusLabel);
        controlPanel.add(startStopBtn);
        topPanel.add(controlPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel - charts and tables
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        // CPU panel
        JPanel cpuPanel = new JPanel(new BorderLayout(5, 5));
        cpuPanel.setBorder(BorderFactory.createTitledBorder("CPU"));
        JPanel cpuInfoPanel = new JPanel(new BorderLayout());
        cpuInfoPanel.add(cpuLabel, BorderLayout.WEST);
        cpuInfoPanel.add(cpuBar, BorderLayout.CENTER);
        cpuPanel.add(cpuInfoPanel, BorderLayout.NORTH);
        cpuPanel.add(cpuChart, BorderLayout.CENTER);
        centerPanel.add(cpuPanel);
        
        // Memory panel
        JPanel memPanel = new JPanel(new BorderLayout(5, 5));
        memPanel.setBorder(BorderFactory.createTitledBorder("内存"));
        JPanel memInfoPanel = new JPanel(new BorderLayout());
        memInfoPanel.add(memLabel, BorderLayout.WEST);
        memInfoPanel.add(memBar, BorderLayout.CENTER);
        memPanel.add(memInfoPanel, BorderLayout.NORTH);
        memPanel.add(memChart, BorderLayout.CENTER);
        centerPanel.add(memPanel);
        
        // Disk panel
        JPanel diskPanel = new JPanel(new BorderLayout());
        diskPanel.setBorder(BorderFactory.createTitledBorder("磁盘"));
        diskPanel.add(new JScrollPane(diskTable), BorderLayout.CENTER);
        centerPanel.add(diskPanel);
        
        // Network panel
        JPanel netPanel = new JPanel(new BorderLayout(5, 5));
        netPanel.setBorder(BorderFactory.createTitledBorder("网络"));
        JPanel netInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        netInfoPanel.add(netRxLabel);
        netInfoPanel.add(netTxLabel);
        netPanel.add(netInfoPanel, BorderLayout.NORTH);
        netPanel.add(netChart, BorderLayout.CENTER);
        centerPanel.add(netPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - process table
        JPanel processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("进程 (按CPU排序)"));
        processPanel.add(new JScrollPane(processTable), BorderLayout.CENTER);
        processPanel.setPreferredSize(new Dimension(0, 180));
        
        add(processPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createLabelPanel(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title + ": ");
        titleLabel.setForeground(Color.GRAY);
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Start monitoring
     */
    public void startMonitoring() {
        if (monitorSession == null) {
            monitorSession = new MonitorSession(sshSession);
            monitorSession.addListener(this);
        }
        
        monitorSession.start();
        startStopBtn.setText("停止监控");
        startStopBtn.setSelected(true);
        statusLabel.setText("运行中");
        statusLabel.setForeground(new Color(0, 180, 0));
        
        logger.info("Monitoring started");
    }
    
    /**
     * Stop monitoring
     */
    public void stopMonitoring() {
        if (monitorSession != null) {
            monitorSession.stop();
        }
        
        startStopBtn.setText("启动监控");
        startStopBtn.setSelected(false);
        statusLabel.setText("已停止");
        statusLabel.setForeground(Color.GRAY);
        
        logger.info("Monitoring stopped");
    }
    
    @Override
    public void onDataReceived(MonitorData data) {
        SwingUtilities.invokeLater(() -> updateUI(data));
    }
    
    @Override
    public void onError(String message) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("错误: " + message);
            statusLabel.setForeground(Color.RED);
        });
    }
    
    /**
     * Update UI with monitoring data
     */
    private void updateUI(MonitorData data) {
        // Overview
        hostLabel.setText(data.getHostname());
        osLabel.setText(data.getOsName());
        uptimeLabel.setText(MonitorData.formatUptime(data.getUptime()));
        loadLabel.setText(String.format("%.2f / %.2f / %.2f", 
            data.getLoadAverage1(), data.getLoadAverage5(), data.getLoadAverage15()));
        
        // CPU
        int cpuUsage = (int) data.getCpuUsage();
        cpuBar.setValue(cpuUsage);
        cpuBar.setString(cpuUsage + "%");
        cpuLabel.setText(String.format("CPU: %d核 %s", data.getCpuCores(), 
            data.getCpuModel() != null ? data.getCpuModel() : ""));
        cpuChart.addValue(data.getCpuUsage());
        
        // Memory
        int memUsage = (int) data.getMemUsagePercent();
        memBar.setValue(memUsage);
        memBar.setString(String.format("%d%% (%s / %s)", memUsage,
            MonitorData.formatBytes(data.getMemUsed()),
            MonitorData.formatBytes(data.getMemTotal())));
        memLabel.setText(String.format("内存: %s 已用", MonitorData.formatBytes(data.getMemUsed())));
        memChart.addValue(data.getMemUsagePercent());
        
        // Disk
        diskModel.setRowCount(0);
        for (MonitorData.DiskInfo disk : data.getDisks()) {
            diskModel.addRow(new Object[]{
                disk.getMountPoint(),
                MonitorData.formatBytes(disk.getTotal()),
                MonitorData.formatBytes(disk.getUsed()),
                MonitorData.formatBytes(disk.getFree()),
                String.format("%.1f%%", disk.getUsagePercent())
            });
        }
        
        // Network
        netRxLabel.setText("下载: " + MonitorData.formatBytes(data.getNetRxSpeed()) + "/s");
        netTxLabel.setText("上传: " + MonitorData.formatBytes(data.getNetTxSpeed()) + "/s");
        // Normalize network speed to 0-100 for chart (max 100MB/s)
        double netPercent = Math.min(100, (data.getNetRxSpeed() + data.getNetTxSpeed()) / 1048576.0);
        netChart.addValue(netPercent);
        
        // Processes
        processModel.setRowCount(0);
        for (MonitorData.ProcessInfo proc : data.getTopProcesses()) {
            processModel.addRow(new Object[]{
                proc.getPid(),
                proc.getUser(),
                String.format("%.1f", proc.getCpuPercent()),
                String.format("%.1f", proc.getMemPercent()),
                proc.getCommand()
            });
        }
    }
    
    /**
     * Close and cleanup
     */
    public void close() {
        stopMonitoring();
    }
    
    /**
     * Simple usage chart component
     */
    private static class UsageChart extends JPanel {
        private final String title;
        private final Color color;
        private final LinkedList<Double> values = new LinkedList<>();
        private static final int MAX_POINTS = 60;
        
        public UsageChart(String title, Color color) {
            this.title = title;
            this.color = color;
            setBackground(new Color(30, 30, 30));
            setPreferredSize(new Dimension(200, 80));
        }
        
        public void addValue(double value) {
            values.addLast(value);
            while (values.size() > MAX_POINTS) {
                values.removeFirst();
            }
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int padding = 5;
            
            // Draw grid
            g2.setColor(new Color(60, 60, 60));
            for (int i = 1; i < 4; i++) {
                int y = padding + (h - 2 * padding) * i / 4;
                g2.drawLine(padding, y, w - padding, y);
            }
            
            // Draw chart
            if (values.size() >= 2) {
                g2.setColor(color);
                
                int pointCount = values.size();
                double xStep = (double) (w - 2 * padding) / (MAX_POINTS - 1);
                
                int[] xPoints = new int[pointCount + 2];
                int[] yPoints = new int[pointCount + 2];
                
                int i = 0;
                for (Double value : values) {
                    int x = padding + (int) ((MAX_POINTS - pointCount + i) * xStep);
                    int y = h - padding - (int) ((h - 2 * padding) * value / 100);
                    xPoints[i + 1] = x;
                    yPoints[i + 1] = y;
                    i++;
                }
                
                // Close polygon
                xPoints[0] = xPoints[1];
                yPoints[0] = h - padding;
                xPoints[pointCount + 1] = xPoints[pointCount];
                yPoints[pointCount + 1] = h - padding;
                
                // Fill
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));
                g2.fillPolygon(xPoints, yPoints, pointCount + 2);
                
                // Line
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2));
                for (int j = 1; j < pointCount; j++) {
                    g2.drawLine(xPoints[j], yPoints[j], xPoints[j + 1], yPoints[j + 1]);
                }
            }
        }
    }
}
