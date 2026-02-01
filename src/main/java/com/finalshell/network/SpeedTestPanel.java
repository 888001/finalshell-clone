package com.finalshell.network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

/**
 * 网络速度测试面板
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Transfer_SpeedTest_UI_DeepAnalysis.md
 */
public class SpeedTestPanel extends JPanel {
    
    private static final String[] TEST_URLS = {
        "https://speed.cloudflare.com/__down?bytes=10000000",
        "https://proof.ovh.net/files/10Mb.dat",
        "https://speedtest.tele2.net/10MB.zip"
    };
    
    private JComboBox<String> serverCombo;
    private JTextField urlField;
    private JButton startBtn;
    private JButton stopBtn;
    
    private JLabel downloadSpeedLabel;
    private JLabel uploadSpeedLabel;
    private JLabel pingLabel;
    private JProgressBar progressBar;
    private JTextArea logArea;

    private SpeedTestCanvas canvas;
    private volatile SpeedTestTask speedTask;
    private final int testDurationSeconds = 10;
    
    private ExecutorService executor;
    private volatile boolean running = false;
    
    public SpeedTestPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
    }
    
    private void initComponents() {
        // 顶部配置面板
        JPanel configPanel = new JPanel(new BorderLayout(5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("测试配置"));
        
        JPanel urlPanel = new JPanel(new BorderLayout(5, 0));
        urlPanel.add(new JLabel("测试服务器:"), BorderLayout.WEST);
        
        serverCombo = new JComboBox<>(new String[]{
            "Cloudflare (全球)",
            "OVH (欧洲)",
            "Tele2 (欧洲)",
            "自定义URL"
        });
        serverCombo.addActionListener(e -> {
            int index = serverCombo.getSelectedIndex();
            urlField.setEnabled(index == 3);
            if (index < TEST_URLS.length) {
                urlField.setText(TEST_URLS[index]);
            }
        });
        urlPanel.add(serverCombo, BorderLayout.CENTER);
        
        urlField = new JTextField(TEST_URLS[0]);
        urlField.setEnabled(false);
        
        JPanel urlTopPanel = new JPanel(new BorderLayout(5, 5));
        urlTopPanel.add(urlPanel, BorderLayout.NORTH);
        urlTopPanel.add(urlField, BorderLayout.CENTER);
        configPanel.add(urlTopPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startBtn = new JButton("开始测试");
        startBtn.addActionListener(e -> startTest());
        btnPanel.add(startBtn);
        
        stopBtn = new JButton("停止");
        stopBtn.setEnabled(false);
        stopBtn.addActionListener(e -> stopTest());
        btnPanel.add(stopBtn);
        
        configPanel.add(btnPanel, BorderLayout.SOUTH);
        add(configPanel, BorderLayout.NORTH);
        
        // 结果面板
        JPanel resultPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        resultPanel.setBorder(BorderFactory.createTitledBorder("测试结果"));
        
        // 下载速度
        JPanel downloadPanel = new JPanel(new BorderLayout());
        downloadPanel.add(new JLabel("下载速度", SwingConstants.CENTER), BorderLayout.NORTH);
        downloadSpeedLabel = new JLabel("-- MB/s", SwingConstants.CENTER);
        downloadSpeedLabel.setFont(downloadSpeedLabel.getFont().deriveFont(24f));
        downloadSpeedLabel.setForeground(new Color(0, 128, 0));
        downloadPanel.add(downloadSpeedLabel, BorderLayout.CENTER);
        resultPanel.add(downloadPanel);
        
        // Ping
        JPanel pingPanel = new JPanel(new BorderLayout());
        pingPanel.add(new JLabel("延迟", SwingConstants.CENTER), BorderLayout.NORTH);
        pingLabel = new JLabel("-- ms", SwingConstants.CENTER);
        pingLabel.setFont(pingLabel.getFont().deriveFont(24f));
        pingLabel.setForeground(new Color(0, 0, 128));
        pingPanel.add(pingLabel, BorderLayout.CENTER);
        resultPanel.add(pingPanel);
        
        // 上传速度
        JPanel uploadPanel = new JPanel(new BorderLayout());
        uploadPanel.add(new JLabel("上传速度", SwingConstants.CENTER), BorderLayout.NORTH);
        uploadSpeedLabel = new JLabel("-- MB/s", SwingConstants.CENTER);
        uploadSpeedLabel.setFont(uploadSpeedLabel.getFont().deriveFont(24f));
        uploadSpeedLabel.setForeground(new Color(128, 0, 0));
        uploadPanel.add(uploadSpeedLabel, BorderLayout.CENTER);
        resultPanel.add(uploadPanel);
        
        // 中间面板
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));

        JPanel resultTopPanel = new JPanel(new BorderLayout(5, 5));
        resultTopPanel.add(resultPanel, BorderLayout.NORTH);
        canvas = new SpeedTestCanvas();
        resultTopPanel.add(canvas, BorderLayout.CENTER);
        centerPanel.add(resultTopPanel, BorderLayout.NORTH);
        
        // 日志
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createTitledBorder("测试日志"));
        centerPanel.add(logScroll, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // 进度条
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("就绪");
        add(progressBar, BorderLayout.SOUTH);
    }
    
    private void startTest() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入测试URL", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        running = true;
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        downloadSpeedLabel.setText("-- MB/s");
        uploadSpeedLabel.setText("-- MB/s");
        pingLabel.setText("-- ms");
        logArea.setText("");
        progressBar.setValue(0);
        progressBar.setString("正在测试...");

        if (canvas != null) {
            canvas.reset();
        }

        SpeedTestTask prev = speedTask;
        if (prev != null) {
            try {
                prev.cancel();
            } catch (Exception ignored) {
            }
            speedTask = null;
        }

        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                // 测试延迟
                log("测试延迟...");
                long ping = testPing(url);
                final long finalPing = ping;
                SwingUtilities.invokeLater(() -> pingLabel.setText(finalPing + " ms"));
                log("延迟: " + ping + " ms");
                
                if (!running) return;
                
                // 测试下载速度
                log("\n测试下载速度...");
                SwingUtilities.invokeLater(() -> {
                    progressBar.setString("测试下载...");
                    progressBar.setValue(0);
                });

                SpeedTestTask task = new SpeedTestTask(url, testDurationSeconds);
                speedTask = task;

                final long[] dlStart = new long[]{0L};
                task.addListener(new SpeedTestTask.SpeedTestListener() {
                    @Override
                    public void onTestStarted() {
                        dlStart[0] = System.currentTimeMillis();
                    }

                    @Override
                    public void onSpeedUpdated(double currentSpeed, double avgSpeed, double maxSpeed) {
                        if (!running) {
                            return;
                        }
                        String speedStr = String.format("%.2f MB/s", currentSpeed);
                        SwingUtilities.invokeLater(() -> {
                            downloadSpeedLabel.setText(speedStr);
                            if (canvas != null) {
                                canvas.setCurrentSpeed(currentSpeed);
                            }
                            long elapsed = System.currentTimeMillis() - dlStart[0];
                            int progress = 0;
                            if (elapsed > 0) {
                                progress = Math.min(49, (int) (elapsed * 50 / (testDurationSeconds * 1000L)));
                            }
                            progressBar.setValue(progress);
                        });
                    }

                    @Override
                    public void onTestCompleted(SpeedTestTask.SpeedTestResult result) {
                        if (!running) {
                            return;
                        }
                        SwingUtilities.invokeLater(() -> progressBar.setValue(50));
                        log("下载平均: " + result.getAvgSpeedStr());
                        log("下载峰值: " + result.getMaxSpeedStr());
                    }

                    @Override
                    public void onTestError(Exception e) {
                        log("下载测速失败: " + (e == null ? "" : e.getMessage()));
                    }
                });

                task.run();
                
                if (!running) return;
                
                // 上传测试（简化版）
                try {
                    String uploadUrl = getUploadUrl(url, serverCombo.getSelectedIndex());
                    log("\n测试上传速度...\n" + uploadUrl);
                    SwingUtilities.invokeLater(() -> progressBar.setString("测试上传..."));
                    double uploadSpeed = testUpload(uploadUrl);
                    final String uploadStr = String.format("%.2f MB/s", uploadSpeed);
                    SwingUtilities.invokeLater(() -> uploadSpeedLabel.setText(uploadStr));
                    log("上传速度: " + uploadStr);
                } catch (Exception e) {
                    log("上传测速失败: " + e.getMessage());
                    SwingUtilities.invokeLater(() -> uploadSpeedLabel.setText("N/A"));
                }
                
                log("\n测试完成!");
                
            } catch (Exception e) {
                log("错误: " + e.getMessage());
            } finally {
                boolean stopped = !running;
                SwingUtilities.invokeLater(() -> {
                    running = false;
                    startBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    progressBar.setValue(100);
                    progressBar.setString(stopped ? "已停止" : "完成");
                });
                speedTask = null;
            }
        });
    }
    
    private void stopTest() {
        running = false;
        SwingUtilities.invokeLater(() -> {
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            progressBar.setString("已停止");
        });
        SpeedTestTask t = speedTask;
        if (t != null) {
            try {
                t.cancel();
            } catch (Exception ignored) {
            }
        }
        if (executor != null) {
            executor.shutdownNow();
        }
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
        
        long startTime = System.currentTimeMillis();
        long totalBytes = 0;
        
        try (InputStream in = conn.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = in.read(buffer)) != -1 && running) {
                totalBytes += bytesRead;
                
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > 0) {
                    double speed = (totalBytes * 1000.0) / elapsed / 1024 / 1024;
                    final int progress = Math.min(99, (int) (elapsed / 100));
                    final String speedStr = String.format("%.2f MB/s", speed);
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        downloadSpeedLabel.setText(speedStr);
                    });
                }
                
                // 测试10秒
                if (System.currentTimeMillis() - startTime > 10000) break;
            }
        } finally {
            conn.disconnect();
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        return (totalBytes * 1000.0) / elapsed / 1024 / 1024;
    }

    private String getUploadUrl(String downloadUrl, int serverIndex) {
        if (serverIndex == 0) {
            return "https://speed.cloudflare.com/__up";
        }
        if (downloadUrl != null && downloadUrl.contains("speed.cloudflare.com") && downloadUrl.contains("__down")) {
            return "https://speed.cloudflare.com/__up";
        }
        return downloadUrl;
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
        java.util.Random r = new java.util.Random();
        r.nextBytes(buffer);

        long startTime = System.currentTimeMillis();
        long totalBytes = 0;
        long lastUpdate = startTime;
        long bytesInInterval = 0;

        try (OutputStream out = conn.getOutputStream()) {
            while (running) {
                out.write(buffer);
                totalBytes += buffer.length;
                bytesInInterval += buffer.length;

                long now = System.currentTimeMillis();
                long elapsed = now - lastUpdate;
                if (elapsed >= 500) {
                    double speed = (bytesInInterval * 1000.0) / elapsed / 1024 / 1024;
                    final int progress = 50 + Math.min(49, (int) ((now - startTime) / 200));
                    final String speedStr = String.format("%.2f MB/s", speed);
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        uploadSpeedLabel.setText(speedStr);
                    });
                    lastUpdate = now;
                    bytesInInterval = 0;
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
        return (totalBytes * 1000.0) / elapsed / 1024 / 1024;
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    public void cleanup() {
        stopTest();
    }
}
