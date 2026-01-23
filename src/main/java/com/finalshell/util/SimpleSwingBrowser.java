package com.finalshell.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

/**
 * 简易Swing浏览器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - SimpleSwingBrowser
 */
public class SimpleSwingBrowser extends JFrame {
    
    private JEditorPane editorPane;
    private JTextField urlField;
    private JButton goButton;
    private JButton backButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JProgressBar progressBar;
    
    private java.util.List<String> history = new java.util.ArrayList<>();
    private int historyIndex = -1;
    
    public SimpleSwingBrowser() {
        this("简易浏览器");
    }
    
    public SimpleSwingBrowser(String title) {
        super(title);
        initUI();
    }
    
    private void initUI() {
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // 工具栏
        JPanel toolBar = new JPanel(new BorderLayout(5, 0));
        toolBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        backButton = new JButton("←");
        backButton.setToolTipText("后退");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);
        
        forwardButton = new JButton("→");
        forwardButton.setToolTipText("前进");
        forwardButton.addActionListener(e -> goForward());
        buttonPanel.add(forwardButton);
        
        refreshButton = new JButton("↻");
        refreshButton.setToolTipText("刷新");
        refreshButton.addActionListener(e -> refresh());
        buttonPanel.add(refreshButton);
        
        toolBar.add(buttonPanel, BorderLayout.WEST);
        
        urlField = new JTextField();
        urlField.addActionListener(e -> loadUrl(urlField.getText()));
        toolBar.add(urlField, BorderLayout.CENTER);
        
        goButton = new JButton("转到");
        goButton.addActionListener(e -> loadUrl(urlField.getText()));
        toolBar.add(goButton, BorderLayout.EAST);
        
        add(toolBar, BorderLayout.NORTH);
        
        // 内容区域
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        JScrollPane scrollPane = new JScrollPane(editorPane);
        add(scrollPane, BorderLayout.CENTER);
        
        // 状态栏
        JPanel statusBar = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        statusBar.add(progressBar, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
        updateButtons();
    }
    
    public void loadUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        final String finalUrl = url;
        urlField.setText(url);
        progressBar.setVisible(true);
        progressBar.setIndeterminate(true);
        
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                editorPane.setPage(finalUrl);
                return null;
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setIndeterminate(false);
                
                // 添加到历史记录
                historyIndex++;
                while (history.size() > historyIndex) {
                    history.remove(history.size() - 1);
                }
                history.add(finalUrl);
                updateButtons();
            }
        }.execute();
    }
    
    public void goBack() {
        if (historyIndex > 0) {
            historyIndex--;
            String url = history.get(historyIndex);
            urlField.setText(url);
            try {
                editorPane.setPage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateButtons();
        }
    }
    
    public void goForward() {
        if (historyIndex < history.size() - 1) {
            historyIndex++;
            String url = history.get(historyIndex);
            urlField.setText(url);
            try {
                editorPane.setPage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateButtons();
        }
    }
    
    public void refresh() {
        if (historyIndex >= 0 && historyIndex < history.size()) {
            String url = history.get(historyIndex);
            try {
                editorPane.setPage(url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void updateButtons() {
        backButton.setEnabled(historyIndex > 0);
        forwardButton.setEnabled(historyIndex < history.size() - 1);
    }
    
    public static void openInSystemBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
