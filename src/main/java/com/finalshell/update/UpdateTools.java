package com.finalshell.update;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 更新工具类
 * 提供版本检查和下载更新的工具方法
 */
public class UpdateTools {
    
    private static final int BUFFER_SIZE = 8192;
    private static final int TIMEOUT = 30000;
    
    public static UpdateConfig checkUpdate(String checkUrl) throws Exception {
        URL url = new URL(checkUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        
        try (InputStream is = conn.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return parseUpdateConfig(sb.toString());
        } finally {
            conn.disconnect();
        }
    }
    
    private static UpdateConfig parseUpdateConfig(String json) {
        UpdateConfig config = new UpdateConfig();
        // 简单解析JSON（实际应使用JSON库）
        return config;
    }
    
    public static void downloadFile(String urlStr, File destFile, DownloadListener listener) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(TIMEOUT);
        conn.setReadTimeout(TIMEOUT);
        
        long totalSize = conn.getContentLengthLong();
        long downloaded = 0;
        
        try (InputStream is = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
                downloaded += read;
                if (listener != null) {
                    listener.onProgress(downloaded, totalSize);
                }
            }
        } finally {
            conn.disconnect();
        }
        
        if (listener != null) {
            listener.onComplete();
        }
    }
    
    public static int compareVersion(String v1, String v2) {
        String[] parts1 = v1.split("\\.");
        String[] parts2 = v2.split("\\.");
        
        int length = Math.max(parts1.length, parts2.length);
        for (int i = 0; i < length; i++) {
            int num1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int num2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
            
            if (num1 < num2) return -1;
            if (num1 > num2) return 1;
        }
        return 0;
    }
    
    public static boolean needUpdate(String currentVersion, String newVersion) {
        return compareVersion(currentVersion, newVersion) < 0;
    }
    
    public interface DownloadListener {
        void onProgress(long downloaded, long total);
        void onComplete();
        void onError(Exception e);
    }
}
