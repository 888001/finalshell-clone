package com.finalshell.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * HTTP工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Utility_DeepAnalysis.md - HttpTools
 */
public class HttpTools {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpTools.class);
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 30000;
    
    /**
     * GET请求
     */
    public static String get(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setRequestProperty("User-Agent", "FinalShell");
        
        try {
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return readStream(conn.getInputStream());
            } else {
                throw new IOException("HTTP Error: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * POST请求 (JSON)
     */
    public static JSONObject postJson(String urlStr, JSONObject data) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("User-Agent", "FinalShell");
        
        try {
            // 发送数据
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.toJSONString().getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                String response = readStream(conn.getInputStream());
                return JSONObject.parseObject(response);
            } else {
                throw new IOException("HTTP Error: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * POST请求 (表单)
     */
    public static String postForm(String urlStr, String params) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        
        try {
            try (OutputStream os = conn.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return readStream(conn.getInputStream());
            } else {
                throw new IOException("HTTP Error: " + responseCode);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * 下载文件
     */
    public static void download(String urlStr, File destFile, DownloadCallback callback) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        
        try {
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP Error: " + responseCode);
            }
            
            long contentLength = conn.getContentLengthLong();
            long downloaded = 0;
            
            try (InputStream is = conn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(destFile)) {
                
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    downloaded += len;
                    
                    if (callback != null) {
                        callback.onProgress(downloaded, contentLength);
                    }
                }
            }
            
            if (callback != null) {
                callback.onComplete(destFile);
            }
        } finally {
            conn.disconnect();
        }
    }
    
    private static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toString("UTF-8");
    }
    
    /**
     * 下载回调
     */
    public interface DownloadCallback {
        void onProgress(long downloaded, long total);
        void onComplete(File file);
    }
}
