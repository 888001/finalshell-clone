package com.finalshell.util;

import com.finalshell.ui.browser.SwingFXWebView;

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
    
    private SwingFXWebView webView;
    private JTextField urlField;
    private JButton goButton;
    private JButton backButton;
    private JButton forwardButton;
    private JButton refreshButton;
    private JProgressBar progressBar;
    
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

        // 状态栏
        JPanel statusBar = new JPanel(new BorderLayout());
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        statusBar.add(progressBar, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        
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
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                    updateButtons();
                });
            }

            @Override
            public void onLoadFinished(String url) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                    updateButtons();
                });
            }

            @Override
            public void onLoadError(String url, String error) {
                SwingUtilities.invokeLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setIndeterminate(false);
                    updateButtons();
                });
            }

            @Override
            public void onTitleChanged(String title) {
                SwingUtilities.invokeLater(() -> {
                    if (title != null && !title.trim().isEmpty()) {
                        setTitle(title);
                    }
                });
            }

            @Override
            public void onLocationChanged(String url) {
                SwingUtilities.invokeLater(() -> {
                    if (url != null) {
                        urlField.setText(url);
                    }
                    updateButtons();
                });
            }

            @Override
            public void onProgressChanged(double progress) {
                SwingUtilities.invokeLater(() -> {
                    if (progress >= 0 && progress <= 1) {
                        progressBar.setVisible(true);
                        progressBar.setIndeterminate(false);
                        progressBar.setMinimum(0);
                        progressBar.setMaximum(100);
                        progressBar.setValue((int) (progress * 100));
                    }
                });
            }

            @Override
            public void onStatusChanged(String status) {
            }
        });
        add(webView, BorderLayout.CENTER);
        
        updateButtons();
    }
    
    public void loadUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        
        urlField.setText(url);
        if (webView != null) {
            webView.loadUrl(url);
        }
    }
    
    public void goBack() {
        if (webView != null) {
            webView.goBack();
            updateButtons();
        }
    }
    
    public void goForward() {
        if (webView != null) {
            webView.goForward();
            updateButtons();
        }
    }
    
    public void refresh() {
        if (webView != null) {
            webView.reload();
        }
    }
    
    private void updateButtons() {
        backButton.setEnabled(webView != null && webView.canGoBack());
        forwardButton.setEnabled(webView != null && webView.canGoForward());
    }
    
    public static void openInSystemBrowser(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
