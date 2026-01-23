package com.finalshell.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

/**
 * 简单Swing浏览器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - SimpleSwingBrowser
 */
public class SimpleSwingBrowser extends JPanel {
    
    private JTextField urlField;
    private JEditorPane contentPane;
    private JButton goButton;
    private JButton backButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    
    private String currentUrl;
    
    public SimpleSwingBrowser() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        // 工具栏
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        backButton = new JButton("<");
        backButton.setToolTipText("后退");
        backButton.addActionListener(e -> goBack());
        
        forwardButton = new JButton(">");
        forwardButton.setToolTipText("前进");
        forwardButton.addActionListener(e -> goForward());
        
        refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refresh());
        
        urlField = new JTextField();
        urlField.addActionListener(e -> loadUrl(urlField.getText()));
        
        goButton = new JButton("转到");
        goButton.addActionListener(e -> loadUrl(urlField.getText()));
        
        toolbar.add(backButton);
        toolbar.add(forwardButton);
        toolbar.add(refreshButton);
        toolbar.addSeparator();
        toolbar.add(urlField);
        toolbar.add(goButton);
        
        add(toolbar, BorderLayout.NORTH);
        
        // 内容区域
        contentPane = new JEditorPane();
        contentPane.setEditable(false);
        contentPane.setContentType("text/html");
        
        JScrollPane scrollPane = new JScrollPane(contentPane);
        add(scrollPane, BorderLayout.CENTER);
        
        // 状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    public void loadUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        final String finalUrl = url;
        currentUrl = url;
        urlField.setText(url);
        statusLabel.setText("正在加载: " + url);
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                java.net.URL urlObj = new java.net.URL(finalUrl);
                java.io.InputStream is = urlObj.openStream();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                reader.close();
                return sb.toString();
            }
            
            @Override
            protected void done() {
                try {
                    String content = get();
                    contentPane.setText(content);
                    contentPane.setCaretPosition(0);
                    statusLabel.setText("完成");
                } catch (Exception e) {
                    statusLabel.setText("加载失败: " + e.getMessage());
                    contentPane.setText("<html><body><h2>加载失败</h2><p>" + 
                        e.getMessage() + "</p></body></html>");
                }
            }
        };
        worker.execute();
    }
    
    public void goBack() {
        // TODO: 实现历史记录后退
    }
    
    public void goForward() {
        // TODO: 实现历史记录前进
    }
    
    public void refresh() {
        if (currentUrl != null) {
            loadUrl(currentUrl);
        }
    }
    
    public String getCurrentUrl() {
        return currentUrl;
    }
    
    public static void openInSystemBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
