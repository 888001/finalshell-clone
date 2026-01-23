package com.finalshell.network;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.*;
import java.util.regex.*;

/**
 * 路由追踪面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FloatNav_Panel_DeepAnalysis.md
 */
public class TraceroutePanel extends JPanel {
    
    private JTextField hostField;
    private JButton traceBtn;
    private JButton stopBtn;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JProgressBar progressBar;
    private JTextArea logArea;
    
    private ExecutorService executor;
    private Process currentProcess;
    private volatile boolean running = false;
    
    public TraceroutePanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    private void initComponents() {
        // 顶部输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.add(new JLabel("目标主机:"), BorderLayout.WEST);
        
        hostField = new JTextField("www.google.com");
        hostField.addActionListener(e -> startTrace());
        inputPanel.add(hostField, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        traceBtn = new JButton("追踪");
        traceBtn.addActionListener(e -> startTrace());
        btnPanel.add(traceBtn);
        
        stopBtn = new JButton("停止");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(e -> stopTrace());
        btnPanel.add(stopBtn);
        
        inputPanel.add(btnPanel, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);
        
        // 结果表格
        String[] columns = {"跳数", "IP地址", "主机名", "延迟1", "延迟2", "延迟3"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setPreferredSize(new Dimension(0, 200));
        
        // 日志区域
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setPreferredSize(new Dimension(0, 150));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScroll, logScroll);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
        
        // 进度条
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("就绪");
        add(progressBar, BorderLayout.SOUTH);
    }
    
    private void startTrace() {
        String host = hostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入目标主机", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (running) return;
        
        running = true;
        traceBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        tableModel.setRowCount(0);
        logArea.setText("");
        progressBar.setIndeterminate(true);
        progressBar.setString("正在追踪...");
        
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                String os = System.getProperty("os.name").toLowerCase();
                String[] cmd;
                
                if (os.contains("win")) {
                    cmd = new String[]{"tracert", "-d", host};
                } else {
                    cmd = new String[]{"traceroute", "-n", host};
                }
                
                ProcessBuilder pb = new ProcessBuilder(cmd);
                pb.redirectErrorStream(true);
                currentProcess = pb.start();
                
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(currentProcess.getInputStream()));
                
                String line;
                int hop = 0;
                
                while ((line = reader.readLine()) != null && running) {
                    final String logLine = line;
                    SwingUtilities.invokeLater(() -> {
                        logArea.append(logLine + "\n");
                        logArea.setCaretPosition(logArea.getDocument().getLength());
                    });
                    
                    // 解析跳数行
                    Object[] row = parseTraceLine(line, ++hop);
                    if (row != null) {
                        final Object[] finalRow = row;
                        SwingUtilities.invokeLater(() -> tableModel.addRow(finalRow));
                    }
                }
                
                currentProcess.waitFor();
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    logArea.append("错误: " + e.getMessage() + "\n"));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    traceBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    progressBar.setIndeterminate(false);
                    progressBar.setString("完成");
                });
            }
        });
    }
    
    private void stopTrace() {
        running = false;
        if (currentProcess != null) {
            currentProcess.destroyForcibly();
        }
        if (executor != null) {
            executor.shutdownNow();
        }
    }
    
    private Object[] parseTraceLine(String line, int defaultHop) {
        // Windows: "  1    <1 ms    <1 ms    <1 ms  192.168.1.1"
        // Linux:   "1  192.168.1.1 (192.168.1.1)  0.5 ms  0.4 ms  0.3 ms"
        
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            Pattern p = Pattern.compile("\\s*(\\d+)\\s+([<\\d]+\\s*ms|\\*)\\s+([<\\d]+\\s*ms|\\*)\\s+([<\\d]+\\s*ms|\\*)\\s+(\\S+)?");
            Matcher m = p.matcher(line);
            if (m.find()) {
                return new Object[]{
                    m.group(1),
                    m.group(5) != null ? m.group(5) : "*",
                    "",
                    m.group(2),
                    m.group(3),
                    m.group(4)
                };
            }
        } else {
            Pattern p = Pattern.compile("\\s*(\\d+)\\s+(\\S+)\\s+\\((\\S+)\\)\\s+([\\d.]+\\s*ms)\\s+([\\d.]+\\s*ms)\\s+([\\d.]+\\s*ms)");
            Matcher m = p.matcher(line);
            if (m.find()) {
                return new Object[]{
                    m.group(1),
                    m.group(3),
                    m.group(2),
                    m.group(4),
                    m.group(5),
                    m.group(6)
                };
            }
        }
        
        // 超时行
        if (line.contains("* * *") || line.contains("Request timed out")) {
            Pattern hopP = Pattern.compile("^\\s*(\\d+)");
            Matcher hopM = hopP.matcher(line);
            if (hopM.find()) {
                return new Object[]{hopM.group(1), "*", "", "*", "*", "*"};
            }
        }
        
        return null;
    }
    
    public void cleanup() {
        stopTrace();
    }
}
