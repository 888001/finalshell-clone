package com.finalshell.network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * WHOIS查询面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FloatNav_Panel_DeepAnalysis.md, Tools_DeepAnalysis.md
 */
public class WhoisPanel extends JPanel {
    
    // WHOIS服务器列表
    private static final String[][] WHOIS_SERVERS = {
        {"whois.apnic.net", "亚太地区"},
        {"whois.ripe.net", "欧洲"},
        {"whois.arin.net", "北美"},
        {"whois.afrinic.net", "非洲"},
        {"whois.lacnic.net", "拉丁美洲"},
        {"whois.iana.org", "IANA"},
    };
    
    private JTextField queryField;
    private JComboBox<String> serverCombo;
    private JButton queryBtn;
    private JTextArea resultArea;
    private JProgressBar progressBar;
    
    private ExecutorService executor;
    
    public WhoisPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    private void initComponents() {
        // 顶部输入面板
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel queryPanel = new JPanel(new BorderLayout(5, 0));
        queryPanel.add(new JLabel("查询:"), BorderLayout.WEST);
        queryField = new JTextField();
        queryField.addActionListener(e -> doQuery());
        queryPanel.add(queryField, BorderLayout.CENTER);
        
        JPanel serverPanel = new JPanel(new BorderLayout(5, 0));
        serverPanel.add(new JLabel("服务器:"), BorderLayout.WEST);
        serverCombo = new JComboBox<>();
        for (String[] server : WHOIS_SERVERS) {
            serverCombo.addItem(server[1] + " (" + server[0] + ")");
        }
        serverPanel.add(serverCombo, BorderLayout.CENTER);
        
        queryBtn = new JButton("查询");
        queryBtn.addActionListener(e -> doQuery());
        serverPanel.add(queryBtn, BorderLayout.EAST);
        
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.add(queryPanel);
        topPanel.add(serverPanel);
        inputPanel.add(topPanel, BorderLayout.CENTER);
        
        add(inputPanel, BorderLayout.NORTH);
        
        // 结果区域
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);
        
        // 进度条
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("就绪");
        add(progressBar, BorderLayout.SOUTH);
        
        // 提示
        resultArea.setText("输入IP地址或域名进行WHOIS查询\n\n" +
            "支持的查询类型:\n" +
            "• IP地址 (如: 8.8.8.8)\n" +
            "• 域名 (如: google.com)\n" +
            "• AS号 (如: AS15169)");
    }
    
    private void doQuery() {
        String query = queryField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入查询内容", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int serverIndex = serverCombo.getSelectedIndex();
        String server = WHOIS_SERVERS[serverIndex][0];
        
        queryBtn.setEnabled(false);
        resultArea.setText("");
        progressBar.setIndeterminate(true);
        progressBar.setString("正在查询...");
        
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        
        executor.submit(() -> {
            try {
                String result = whoisQuery(server, query);
                SwingUtilities.invokeLater(() -> {
                    resultArea.setText(result);
                    resultArea.setCaretPosition(0);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> 
                    resultArea.setText("查询失败: " + e.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> {
                    queryBtn.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setString("完成");
                });
            }
        });
    }
    
    private String whoisQuery(String server, String query) throws IOException {
        StringBuilder result = new StringBuilder();
        result.append("WHOIS查询: ").append(query).append("\n");
        result.append("服务器: ").append(server).append("\n");
        result.append("─".repeat(50)).append("\n\n");
        
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(server, 43), 10000);
            socket.setSoTimeout(30000);
            
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println(query);
            
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line).append("\n");
            }
        }
        
        return result.toString();
    }
    
    public void cleanup() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }
}
