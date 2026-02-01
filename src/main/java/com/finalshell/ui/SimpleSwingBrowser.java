package com.finalshell.ui;

import com.finalshell.ui.browser.SwingFXWebView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.*;

/**
 * 简单Swing浏览器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - SimpleSwingBrowser
 */
public class SimpleSwingBrowser extends JPanel {
    
    private JTextField urlField;
    private SwingFXWebView webView;
    private JButton goButton;
    private JButton backButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JLabel statusLabel;
    
    private String currentUrl;
    private java.util.List<String> history = new ArrayList<>();
    private int historyIndex = -1;
    
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

        // 状态栏
        statusLabel = new JLabel("就绪");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        add(statusLabel, BorderLayout.SOUTH);
        
        // 内容区域
        webView = new SwingFXWebView();
        String ua = System.getProperty("embedded.browser.userAgent");
        if (ua != null && !ua.trim().isEmpty()) {
            webView.setUserAgent(ua);
        }
        webView.setListener(new SwingFXWebView.ExtendedWebViewListener() {
            @Override
            public void onLoadStart(String url) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("正在加载: " + url);
                    updateNavigationButtons();
                });
            }

            @Override
            public void onLoadFinished(String url) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("完成");
                    updateNavigationButtons();
                });
            }

            @Override
            public void onLoadError(String url, String error) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("加载失败: " + error);
                    updateNavigationButtons();
                });
            }

            @Override
            public void onTitleChanged(String title) {
            }

            @Override
            public void onLocationChanged(String url) {
                SwingUtilities.invokeLater(() -> {
                    if (url != null) {
                        currentUrl = url;
                        urlField.setText(url);
                    }
                    updateNavigationButtons();
                });
            }

            @Override
            public void onProgressChanged(double progress) {
            }

            @Override
            public void onStatusChanged(String status) {
            }
        });
        add(webView, BorderLayout.CENTER);
    }
    
    public void loadUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        currentUrl = url;
        urlField.setText(url);
        statusLabel.setText("正在加载: " + url);
        if (webView != null) {
            webView.loadUrl(url);
        }
    }
    
    public void goBack() {
        if (webView != null) {
            webView.goBack();
            updateNavigationButtons();
        }
    }
    
    public void goForward() {
        if (webView != null) {
            webView.goForward();
            updateNavigationButtons();
        }
    }
    
    private void updateNavigationButtons() {
        backButton.setEnabled(webView != null && webView.canGoBack());
        forwardButton.setEnabled(webView != null && webView.canGoForward());
    }
    
    public void refresh() {
        if (currentUrl != null) {
            if (webView != null) {
                webView.reload();
            } else {
                loadUrl(currentUrl);
            }
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
