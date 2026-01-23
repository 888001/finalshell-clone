package com.finalshell.network;

import com.finalshell.ssh.SSHSession;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络工具面板
 */
public class NetworkPanel extends JPanel {
    private SSHSession session;
    private NetworkTool networkTool;
    
    private JTabbedPane tabbedPane;
    
    // Ping
    private JTextField pingHostField;
    private JSpinner pingCountSpinner;
    private JTextArea pingOutput;
    private JCheckBox remoteCheck;
    
    // Port Scan
    private JTextField scanHostField;
    private JSpinner startPortSpinner;
    private JSpinner endPortSpinner;
    private JTable portTable;
    private DefaultTableModel portTableModel;
    private JProgressBar scanProgress;
    
    // Traceroute
    private JTextField traceHostField;
    private JTextArea traceOutput;
    
    // DNS
    private JTextField dnsHostField;
    private JTextArea dnsOutput;
    
    private JButton actionBtn;
    private JButton cancelBtn;
    
    public NetworkPanel() {
        this(null);
    }
    
    public NetworkPanel(SSHSession session) {
        this.session = session;
        this.networkTool = new NetworkTool();
        
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        tabbedPane = new JTabbedPane();
        
        // Ping面板
        tabbedPane.addTab("Ping", createPingPanel());
        
        // 端口扫描面板
        tabbedPane.addTab("端口扫描", createPortScanPanel());
        
        // Traceroute面板
        tabbedPane.addTab("Traceroute", createTraceroutePanel());
        
        // DNS面板
        tabbedPane.addTab("DNS查询", createDnsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        actionBtn = new JButton("执行");
        cancelBtn = new JButton("取消");
        cancelBtn.setEnabled(false);
        
        actionBtn.addActionListener(e -> executeAction());
        cancelBtn.addActionListener(e -> cancelAction());
        
        buttonPanel.add(actionBtn);
        buttonPanel.add(cancelBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPingPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("主机:"));
        pingHostField = new JTextField(20);
        topPanel.add(pingHostField);
        topPanel.add(new JLabel("次数:"));
        pingCountSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        topPanel.add(pingCountSpinner);
        remoteCheck = new JCheckBox("远程执行", false);
        remoteCheck.setEnabled(session != null && session.isConnected());
        topPanel.add(remoteCheck);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        pingOutput = new JTextArea();
        pingOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        pingOutput.setEditable(false);
        panel.add(new JScrollPane(pingOutput), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPortScanPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("主机:"));
        scanHostField = new JTextField(15);
        topPanel.add(scanHostField);
        topPanel.add(new JLabel("端口范围:"));
        startPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535, 1));
        topPanel.add(startPortSpinner);
        topPanel.add(new JLabel("-"));
        endPortSpinner = new JSpinner(new SpinnerNumberModel(1024, 1, 65535, 1));
        topPanel.add(endPortSpinner);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // 结果表格
        String[] columns = {"端口", "状态", "服务"};
        portTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        portTable = new JTable(portTableModel);
        panel.add(new JScrollPane(portTable), BorderLayout.CENTER);
        
        // 进度条
        scanProgress = new JProgressBar(0, 100);
        scanProgress.setStringPainted(true);
        panel.add(scanProgress, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createTraceroutePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("目标主机:"));
        traceHostField = new JTextField(25);
        topPanel.add(traceHostField);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        traceOutput = new JTextArea();
        traceOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        traceOutput.setEditable(false);
        panel.add(new JScrollPane(traceOutput), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDnsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.add(new JLabel("主机名/IP:"));
        dnsHostField = new JTextField(25);
        topPanel.add(dnsHostField);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        dnsOutput = new JTextArea();
        dnsOutput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        dnsOutput.setEditable(false);
        panel.add(new JScrollPane(dnsOutput), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void executeAction() {
        int tabIndex = tabbedPane.getSelectedIndex();
        
        actionBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
        
        switch (tabIndex) {
            case 0: executePing(); break;
            case 1: executePortScan(); break;
            case 2: executeTraceroute(); break;
            case 3: executeDns(); break;
        }
    }
    
    private void cancelAction() {
        networkTool.cancel();
        actionComplete();
    }
    
    private void actionComplete() {
        SwingUtilities.invokeLater(() -> {
            actionBtn.setEnabled(true);
            cancelBtn.setEnabled(false);
        });
    }
    
    private void executePing() {
        String host = pingHostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机名", "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        int count = (Integer) pingCountSpinner.getValue();
        pingOutput.setText("");
        
        NetworkTool.PingCallback callback = new NetworkTool.PingCallback() {
            @Override
            public void onStart() {
                appendPing("正在 Ping " + host + " ...\n\n");
            }
            
            @Override
            public void onReply(String host, int seq, long time) {
                appendPing(String.format("来自 %s 的回复: seq=%d time=%dms\n", host, seq, time));
            }
            
            @Override
            public void onTimeout(String host, int seq) {
                appendPing(String.format("请求超时: seq=%d\n", seq));
            }
            
            @Override
            public void onOutput(String line) {
                appendPing(line + "\n");
            }
            
            @Override
            public void onComplete() {
                appendPing("\nPing 完成\n");
                actionComplete();
            }
            
            @Override
            public void onError(String error) {
                appendPing("\n错误: " + error + "\n");
                actionComplete();
            }
        };
        
        if (remoteCheck.isSelected() && session != null && session.isConnected()) {
            networkTool.remotePing(session, host, count, callback);
        } else {
            networkTool.ping(host, count, callback);
        }
    }
    
    private void appendPing(String text) {
        SwingUtilities.invokeLater(() -> pingOutput.append(text));
    }
    
    private void executePortScan() {
        String host = scanHostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机名", "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        int startPort = (Integer) startPortSpinner.getValue();
        int endPort = (Integer) endPortSpinner.getValue();
        
        if (startPort > endPort) {
            JOptionPane.showMessageDialog(this, "起始端口不能大于结束端口", "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        portTableModel.setRowCount(0);
        scanProgress.setValue(0);
        
        List<int[]> openPorts = new ArrayList<>();
        
        networkTool.portScan(host, startPort, endPort, 500, new NetworkTool.PortScanCallback() {
            int scanned = 0;
            int total = 0;
            
            @Override
            public void onStart(int totalPorts) {
                total = totalPorts;
            }
            
            @Override
            public void onPortScanned(int port, boolean open) {
                scanned++;
                if (open) {
                    SwingUtilities.invokeLater(() -> {
                        portTableModel.addRow(new Object[]{port, "开放", getServiceName(port)});
                    });
                }
                SwingUtilities.invokeLater(() -> {
                    scanProgress.setValue((int) ((scanned * 100.0) / total));
                });
            }
            
            @Override
            public void onComplete() {
                SwingUtilities.invokeLater(() -> {
                    scanProgress.setValue(100);
                    JOptionPane.showMessageDialog(NetworkPanel.this, 
                        "扫描完成，发现 " + portTableModel.getRowCount() + " 个开放端口",
                        "完成", JOptionPane.INFORMATION_MESSAGE);
                });
                actionComplete();
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(NetworkPanel.this, "错误: " + error, 
                        "错误", JOptionPane.ERROR_MESSAGE);
                });
                actionComplete();
            }
        });
    }
    
    private String getServiceName(int port) {
        switch (port) {
            case 21: return "FTP";
            case 22: return "SSH";
            case 23: return "Telnet";
            case 25: return "SMTP";
            case 53: return "DNS";
            case 80: return "HTTP";
            case 110: return "POP3";
            case 143: return "IMAP";
            case 443: return "HTTPS";
            case 445: return "SMB";
            case 3306: return "MySQL";
            case 3389: return "RDP";
            case 5432: return "PostgreSQL";
            case 6379: return "Redis";
            case 8080: return "HTTP-ALT";
            case 27017: return "MongoDB";
            default: return "";
        }
    }
    
    private void executeTraceroute() {
        String host = traceHostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机名", "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        if (session == null || !session.isConnected()) {
            JOptionPane.showMessageDialog(this, "需要SSH连接才能执行远程Traceroute", 
                "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        traceOutput.setText("正在执行 traceroute " + host + " ...\n\n");
        
        networkTool.traceroute(session, host, new NetworkTool.TraceCallback() {
            @Override
            public void onStart() {}
            
            @Override
            public void onHop(String line) {
                SwingUtilities.invokeLater(() -> traceOutput.append(line + "\n"));
            }
            
            @Override
            public void onComplete() {
                SwingUtilities.invokeLater(() -> traceOutput.append("\n完成\n"));
                actionComplete();
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> traceOutput.append("\n错误: " + error + "\n"));
                actionComplete();
            }
        });
    }
    
    private void executeDns() {
        String host = dnsHostField.getText().trim();
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入主机名或IP", "错误", JOptionPane.ERROR_MESSAGE);
            actionComplete();
            return;
        }
        
        dnsOutput.setText("正在查询 " + host + " ...\n\n");
        
        // 判断是IP还是域名
        boolean isIp = host.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        
        NetworkTool.DnsCallback callback = new NetworkTool.DnsCallback() {
            @Override
            public void onSuccess(String query, String[] results) {
                SwingUtilities.invokeLater(() -> {
                    for (String r : results) {
                        dnsOutput.append(r + "\n");
                    }
                    dnsOutput.append("\n查询完成\n");
                });
                actionComplete();
            }
            
            @Override
            public void onError(String error) {
                SwingUtilities.invokeLater(() -> dnsOutput.append("错误: " + error + "\n"));
                actionComplete();
            }
        };
        
        if (isIp) {
            networkTool.reverseDns(host, callback);
        } else {
            networkTool.dnsLookup(host, callback);
        }
    }
    
    public void setSession(SSHSession session) {
        this.session = session;
        remoteCheck.setEnabled(session != null && session.isConnected());
    }
    
    public void cleanup() {
        networkTool.shutdown();
    }
}
