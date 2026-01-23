package com.finalshell.monitor;

import com.finalshell.ssh.SSHSession;
import com.finalshell.monitor.parser.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.concurrent.*;

/**
 * 系统信息监控面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Panel_Module_Analysis.md - SysInfoPanel
 */
public class SysInfoPanel extends JPanel {
    
    private final SSHSession session;
    private final MonitorData monitorData;
    
    private JLabel cpuLabel;
    private JProgressBar cpuBar;
    private JLabel memLabel;
    private JProgressBar memBar;
    private JLabel swapLabel;
    private JProgressBar swapBar;
    private JLabel loadLabel;
    private JLabel uptimeLabel;
    private JLabel networkLabel;
    
    private JPanel diskPanel;
    
    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;
    
    public SysInfoPanel(SSHSession session) {
        this.session = session;
        this.monitorData = new MonitorData();
        
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    private void initComponents() {
        // 顶部信息
        JPanel topPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("系统概览"));
        
        // CPU
        JPanel cpuPanel = createMetricPanel("CPU使用率:", cpuBar = new JProgressBar(0, 100));
        cpuLabel = (JLabel) cpuPanel.getComponent(0);
        topPanel.add(cpuPanel);
        
        // 内存
        JPanel memPanel = createMetricPanel("内存使用:", memBar = new JProgressBar(0, 100));
        memLabel = (JLabel) memPanel.getComponent(0);
        topPanel.add(memPanel);
        
        // 交换空间
        JPanel swapPanel = createMetricPanel("交换空间:", swapBar = new JProgressBar(0, 100));
        swapLabel = (JLabel) swapPanel.getComponent(0);
        topPanel.add(swapPanel);
        
        // 负载
        JPanel loadPanel = new JPanel(new BorderLayout());
        loadLabel = new JLabel("系统负载: -");
        loadPanel.add(loadLabel, BorderLayout.CENTER);
        topPanel.add(loadPanel);
        
        // 运行时间
        JPanel uptimePanel = new JPanel(new BorderLayout());
        uptimeLabel = new JLabel("运行时间: -");
        uptimePanel.add(uptimeLabel, BorderLayout.CENTER);
        topPanel.add(uptimePanel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // 磁盘信息
        diskPanel = new JPanel();
        diskPanel.setLayout(new BoxLayout(diskPanel, BoxLayout.Y_AXIS));
        diskPanel.setBorder(BorderFactory.createTitledBorder("磁盘使用"));
        
        JScrollPane diskScroll = new JScrollPane(diskPanel);
        diskScroll.setPreferredSize(new Dimension(300, 200));
        add(diskScroll, BorderLayout.CENTER);
        
        // 网络信息
        JPanel netPanel = new JPanel(new BorderLayout());
        netPanel.setBorder(BorderFactory.createTitledBorder("网络"));
        networkLabel = new JLabel("网络流量: -");
        netPanel.add(networkLabel, BorderLayout.CENTER);
        add(netPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createMetricPanel(String labelText, JProgressBar bar) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 20));
        panel.add(label, BorderLayout.WEST);
        
        bar.setStringPainted(true);
        panel.add(bar, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void startMonitoring() {
        if (running) return;
        running = true;
        
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refresh, 0, 3, TimeUnit.SECONDS);
    }
    
    public void stopMonitoring() {
        running = false;
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }
    
    private void refresh() {
        if (session == null || !session.isConnected()) {
            return;
        }
        
        try {
            // 获取CPU使用率
            String procStat = session.executeCommand("cat /proc/stat | head -1");
            ProcStatParser cpuParser = new ProcStatParser();
            double cpuUsage = cpuParser.parse(procStat);
            
            // 获取内存信息
            String freeOutput = session.executeCommand("free -b");
            FreeParser freeParser = new FreeParser();
            FreeParser.MemoryInfo memInfo = freeParser.parse(freeOutput);
            
            // 获取磁盘信息
            String dfOutput = session.executeCommand("df -B1");
            DfParser dfParser = new DfParser();
            java.util.List<DfParser.DiskInfo> diskList = dfParser.parse(dfOutput);
            
            // 获取负载
            String uptimeOutput = session.executeCommand("uptime");
            UptimeParser uptimeParser = new UptimeParser();
            UptimeParser.UptimeInfo uptimeInfo = uptimeParser.parse(uptimeOutput);
            
            // 更新UI
            SwingUtilities.invokeLater(() -> {
                // CPU
                int cpuPercent = (int) cpuUsage;
                cpuBar.setValue(cpuPercent);
                cpuBar.setString(cpuPercent + "%");
                cpuLabel.setText("CPU使用率:");
                
                // 内存
                if (memInfo != null) {
                    int memPercent = (int) memInfo.getUsedPercent();
                    memBar.setValue(memPercent);
                    memBar.setString(String.format("%d%% (%.1f/%.1f GB)", 
                        memPercent, 
                        memInfo.getUsed() / 1024.0 / 1024.0 / 1024.0,
                        memInfo.getTotal() / 1024.0 / 1024.0 / 1024.0));
                    
                    int swapPercent = (int) memInfo.getSwapUsedPercent();
                    swapBar.setValue(swapPercent);
                    swapBar.setString(swapPercent + "%");
                }
                
                // 负载
                if (uptimeInfo != null) {
                    loadLabel.setText(String.format("系统负载: %.2f, %.2f, %.2f",
                        uptimeInfo.getLoad1(), uptimeInfo.getLoad5(), uptimeInfo.getLoad15()));
                    uptimeLabel.setText("运行时间: " + uptimeInfo.getUptimeString());
                }
                
                // 磁盘
                diskPanel.removeAll();
                for (DfParser.DiskInfo disk : diskList) {
                    JPanel diskItem = new JPanel(new BorderLayout(5, 0));
                    diskItem.add(new JLabel(disk.getMountPoint()), BorderLayout.WEST);
                    JProgressBar diskBar = new JProgressBar(0, 100);
                    diskBar.setValue((int) disk.getUsedPercent());
                    diskBar.setStringPainted(true);
                    diskBar.setString(String.format("%d%% (%s/%s)", 
                        (int) disk.getUsedPercent(),
                        DfParser.formatSize(disk.getUsed()),
                        DfParser.formatSize(disk.getTotal())));
                    diskItem.add(diskBar, BorderLayout.CENTER);
                    diskPanel.add(diskItem);
                }
                diskPanel.revalidate();
                diskPanel.repaint();
            });
            
        } catch (Exception e) {
            // 忽略监控错误
        }
    }
    
    public void cleanup() {
        stopMonitoring();
    }
}
