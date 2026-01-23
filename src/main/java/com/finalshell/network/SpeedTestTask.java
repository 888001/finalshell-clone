package com.finalshell.network;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 网络速度测试任务
 * 用于测试下载速度和上传速度
 */
public class SpeedTestTask implements Runnable {
    
    private String testUrl;
    private int testDuration;
    private List<SpeedTestListener> listeners;
    private AtomicBoolean running;
    private AtomicBoolean cancelled;
    
    private long totalBytes;
    private long startTime;
    private double currentSpeed;
    private double maxSpeed;
    private double avgSpeed;
    
    public SpeedTestTask(String testUrl) {
        this(testUrl, 10);
    }
    
    public SpeedTestTask(String testUrl, int testDuration) {
        this.testUrl = testUrl;
        this.testDuration = testDuration;
        this.listeners = new ArrayList<>();
        this.running = new AtomicBoolean(false);
        this.cancelled = new AtomicBoolean(false);
    }
    
    public void addListener(SpeedTestListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SpeedTestListener listener) {
        listeners.remove(listener);
    }
    
    public void cancel() {
        cancelled.set(true);
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    @Override
    public void run() {
        if (running.get()) {
            return;
        }
        
        running.set(true);
        cancelled.set(false);
        totalBytes = 0;
        startTime = System.currentTimeMillis();
        maxSpeed = 0;
        
        fireTestStarted();
        
        try {
            runDownloadTest();
        } catch (Exception e) {
            fireTestError(e);
        } finally {
            running.set(false);
            fireTestCompleted();
        }
    }
    
    private void runDownloadTest() throws Exception {
        URL url = new URL(testUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(30000);
        conn.setRequestProperty("User-Agent", "FinalShell SpeedTest/1.0");
        
        try (InputStream is = conn.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            long lastUpdate = System.currentTimeMillis();
            long bytesInInterval = 0;
            
            while ((bytesRead = is.read(buffer)) != -1 && !cancelled.get()) {
                totalBytes += bytesRead;
                bytesInInterval += bytesRead;
                
                long now = System.currentTimeMillis();
                long elapsed = now - lastUpdate;
                
                if (elapsed >= 500) {
                    currentSpeed = (bytesInInterval * 1000.0) / elapsed / 1024 / 1024;
                    if (currentSpeed > maxSpeed) {
                        maxSpeed = currentSpeed;
                    }
                    
                    long totalElapsed = now - startTime;
                    avgSpeed = (totalBytes * 1000.0) / totalElapsed / 1024 / 1024;
                    
                    fireSpeedUpdated(currentSpeed, avgSpeed, maxSpeed);
                    
                    lastUpdate = now;
                    bytesInInterval = 0;
                }
                
                if ((now - startTime) >= testDuration * 1000) {
                    break;
                }
            }
        } finally {
            conn.disconnect();
        }
        
        long totalElapsed = System.currentTimeMillis() - startTime;
        avgSpeed = (totalBytes * 1000.0) / totalElapsed / 1024 / 1024;
    }
    
    private void fireTestStarted() {
        for (SpeedTestListener l : listeners) {
            l.onTestStarted();
        }
    }
    
    private void fireSpeedUpdated(double current, double avg, double max) {
        for (SpeedTestListener l : listeners) {
            l.onSpeedUpdated(current, avg, max);
        }
    }
    
    private void fireTestCompleted() {
        SpeedTestResult result = new SpeedTestResult(avgSpeed, maxSpeed, totalBytes);
        for (SpeedTestListener l : listeners) {
            l.onTestCompleted(result);
        }
    }
    
    private void fireTestError(Exception e) {
        for (SpeedTestListener l : listeners) {
            l.onTestError(e);
        }
    }
    
    public double getCurrentSpeed() {
        return currentSpeed;
    }
    
    public double getMaxSpeed() {
        return maxSpeed;
    }
    
    public double getAvgSpeed() {
        return avgSpeed;
    }
    
    public long getTotalBytes() {
        return totalBytes;
    }
    
    public interface SpeedTestListener {
        void onTestStarted();
        void onSpeedUpdated(double currentSpeed, double avgSpeed, double maxSpeed);
        void onTestCompleted(SpeedTestResult result);
        void onTestError(Exception e);
    }
    
    public static class SpeedTestResult {
        private double avgSpeed;
        private double maxSpeed;
        private long totalBytes;
        
        public SpeedTestResult(double avgSpeed, double maxSpeed, long totalBytes) {
            this.avgSpeed = avgSpeed;
            this.maxSpeed = maxSpeed;
            this.totalBytes = totalBytes;
        }
        
        public double getAvgSpeed() {
            return avgSpeed;
        }
        
        public double getMaxSpeed() {
            return maxSpeed;
        }
        
        public long getTotalBytes() {
            return totalBytes;
        }
        
        public String getAvgSpeedStr() {
            return String.format("%.2f MB/s", avgSpeed);
        }
        
        public String getMaxSpeedStr() {
            return String.format("%.2f MB/s", maxSpeed);
        }
    }
}
