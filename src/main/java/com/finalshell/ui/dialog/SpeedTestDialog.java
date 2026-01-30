package com.finalshell.ui.dialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 速度测试对话框
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Dialog_Tree_Module_Analysis.md
 */
public class SpeedTestDialog extends JDialog {
    
    private JTextField hostField;
    private JButton startButton;
    private JButton stopButton;
    private JProgressBar downloadProgress;
    private JProgressBar uploadProgress;
    private JLabel downloadLabel;
    private JLabel uploadLabel;
    private JLabel statusLabel;
    private volatile boolean running = false;
    private ExecutorService executor;
    private Future<?> testFuture;
    
    public SpeedTestDialog(Frame owner) {
        super(owner, "网络速度测试", true);
        initUI();
    }
    
    private void initUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // 主机输入
        JPanel hostPanel = new JPanel(new BorderLayout(5, 0));
        hostPanel.add(new JLabel("测试服务器:"), BorderLayout.WEST);
        hostField = new JTextField("speedtest.tele2.net");
        hostPanel.add(hostField, BorderLayout.CENTER);
        hostPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(hostPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 下载速度
        JPanel downloadPanel = new JPanel(new BorderLayout(5, 0));
        downloadPanel.add(new JLabel("下载速度:"), BorderLayout.WEST);
        downloadLabel = new JLabel("-- Mbps");
        downloadPanel.add(downloadLabel, BorderLayout.EAST);
        downloadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mainPanel.add(downloadPanel);
        
        downloadProgress = new JProgressBar(0, 100);
        downloadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        mainPanel.add(downloadProgress);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 上传速度
        JPanel uploadPanel = new JPanel(new BorderLayout(5, 0));
        uploadPanel.add(new JLabel("上传速度:"), BorderLayout.WEST);
        uploadLabel = new JLabel("-- Mbps");
        uploadPanel.add(uploadLabel, BorderLayout.EAST);
        uploadPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        mainPanel.add(uploadPanel);
        
        uploadProgress = new JProgressBar(0, 100);
        uploadProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        mainPanel.add(uploadProgress);
        mainPanel.add(Box.createVerticalStrut(15));
        
        // 状态
        statusLabel = new JLabel("就绪");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        startButton = new JButton("开始测试");
        startButton.addActionListener(e -> startTest());
        buttonPanel.add(startButton);
        
        stopButton = new JButton("停止");
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopTest());
        buttonPanel.add(stopButton);
        
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void startTest() {
        running = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        downloadProgress.setValue(0);
        uploadProgress.setValue(0);
        downloadLabel.setText("测试中...");
        uploadLabel.setText("等待...");
        statusLabel.setText("正在进行下载测试...");

        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
        testFuture = executor.submit(() -> {
            try {
                String url = normalizeUrl(hostField.getText());
                statusLabelSet("正在测试延迟...");
                long ping = testPing(url);
                statusLabelSet("延迟: " + ping + " ms，正在进行下载测试...");

                // 模拟下载测试
                double downloadSpeed = testDownload(url);
                SwingUtilities.invokeLater(() -> downloadLabel.setText(String.format("%.2f Mbps", downloadSpeed)));

                if (!running) {
                    return;
                }

                statusLabelSet("正在进行上传测试...");
                String uploadUrl = getUploadUrl(url);
                // 模拟上传测试
                double uploadSpeed = testUpload(uploadUrl);
                SwingUtilities.invokeLater(() -> uploadLabel.setText(String.format("%.2f Mbps", uploadSpeed)));

                statusLabelSet("测试完成");
            } catch (Exception e) {
                statusLabelSet("错误: " + e.getMessage());
            } finally {
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        });
    }
    
    private void stopTest() {
        running = false;
        statusLabel.setText("已停止");
        if (testFuture != null) {
            testFuture.cancel(true);
            testFuture = null;
        }
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    @Override
    public void dispose() {
        stopTest();
        super.dispose();
    }

    private void statusLabelSet(String text) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(text));
    }

    private String normalizeUrl(String input) {
        String s = input == null ? "" : input.trim();
        if (s.isEmpty()) {
            return "https://speed.cloudflare.com/__down?bytes=10000000";
        }
        if (s.startsWith("http://") || s.startsWith("https://")) {
            return s;
        }
        return "https://" + s;
    }

    private String getUploadUrl(String downloadUrl) {
        if (downloadUrl != null && downloadUrl.contains("speed.cloudflare.com") && downloadUrl.contains("__down")) {
            return "https://speed.cloudflare.com/__up";
        }
        return downloadUrl;
    }

    private long testPing(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        long start = System.currentTimeMillis();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.connect();
        conn.getResponseCode();
        conn.disconnect();
        return System.currentTimeMillis() - start;
    }

    private double testDownload(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("User-Agent", "FinalShell SpeedTest/1.0");

        long startTime = System.currentTimeMillis();
        long totalBytes = 0;

        try (InputStream in = conn.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1 && running && !Thread.currentThread().isInterrupted()) {
                totalBytes += bytesRead;
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > 0) {
                    double speed = (totalBytes * 8.0 / 1000000) / (elapsed / 1000.0);
                    int progress = Math.min(100, (int) (elapsed / 100));
                    SwingUtilities.invokeLater(() -> {
                        downloadProgress.setValue(progress);
                        downloadLabel.setText(String.format("%.2f Mbps", speed));
                    });
                }

                if (System.currentTimeMillis() - startTime > 10000) {
                    break;
                }
            }
        } finally {
            conn.disconnect();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return (totalBytes * 8.0 / 1000000) / (elapsed / 1000.0);
    }

    private double testUpload(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(60000);
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setRequestProperty("User-Agent", "FinalShell SpeedTest/1.0");
        conn.setChunkedStreamingMode(64 * 1024);

        byte[] buffer = new byte[64 * 1024];
        new Random().nextBytes(buffer);

        long startTime = System.currentTimeMillis();
        long totalBytes = 0;
        long lastUpdate = startTime;
        long bytesInInterval = 0;

        try (OutputStream out = conn.getOutputStream()) {
            while (running && !Thread.currentThread().isInterrupted()) {
                out.write(buffer);
                totalBytes += buffer.length;
                bytesInInterval += buffer.length;

                long now = System.currentTimeMillis();
                long interval = now - lastUpdate;
                if (interval >= 500) {
                    double speed = (bytesInInterval * 8.0 / 1000000) / (interval / 1000.0);
                    int progress = Math.min(100, (int) ((now - startTime) / 100));
                    SwingUtilities.invokeLater(() -> {
                        uploadProgress.setValue(progress);
                        uploadLabel.setText(String.format("%.2f Mbps", speed));
                    });
                    bytesInInterval = 0;
                    lastUpdate = now;
                }

                if (now - startTime > 10000) {
                    break;
                }
            }
            out.flush();
        }

        try {
            InputStream in = conn.getInputStream();
            if (in != null) {
                in.close();
            }
        } catch (Exception ignored) {
        } finally {
            conn.disconnect();
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return (totalBytes * 8.0 / 1000000) / (elapsed / 1000.0);
    }
}
