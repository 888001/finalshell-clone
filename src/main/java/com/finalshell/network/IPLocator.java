package com.finalshell.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * IP地理位置查询器 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/IPLoc.java (53行)
 * 包含HTTP请求和JSON解析功能
 */
public class IPLocator {
    
    private static final Logger logger = LoggerFactory.getLogger(IPLocator.class);
    
    // IP查询API地址 - 对齐原版myssh
    private static final String API_URL = "http://ip.huomao.com/ip?ip=";
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY = 1000;

    /**
     * 查询IP地理位置 - 对齐原版myssh逻辑
     */
    public static IPInfo lookupLocation(String ip) {
        IPInfo result = new IPInfo();
        boolean downloaded = false;
        
        // 最多重试10次 - 对齐原版myssh
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                byte[] data = httpGetBytes(API_URL + ip);
                String str = new String(data, StandardCharsets.UTF_8);
                JSONObject json = JSONObject.parseObject(str);
                
                if (!json.containsKey("country")) break;
                
                String country = json.getString("country");
                country = cleanLocationString(country);
                String city = json.getString("city");
                city = cleanLocationString(city);
                String province = json.getString("province");
                
                result.setCountry(country);
                result.setCity(city);
                result.setProvince(province);
                result.setIsp(json.getString("isp"));
                
                downloaded = true;
                break;
            } catch (Exception e) {
                logger.error("IP查询失败，重试 " + (i + 1) + "/" + MAX_RETRIES, e);
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        return downloaded ? result : null;
    }
    
    /**
     * 清理位置字符串 - 对齐原版myssh逻辑
     */
    private static String cleanLocationString(String str) {
        if (str == null) return "";
        // 这里可以添加原版myssh中的字符串清理逻辑
        return str.trim();
    }
    
    /**
     * 获取本机公网IP
     */
    public static String getPublicIP() {
        String[] services = {
            "https://api.ipify.org",
            "https://icanhazip.com",
            "https://ifconfig.me/ip"
        };
        
        for (String service : services) {
            try {
                String ip = httpGet(service);
                if (ip != null) {
                    return ip.trim();
                }
            } catch (Exception e) {
                logger.debug("Failed to get public IP from: " + service, e);
            }
        }
        return null;
    }
    
    /**
     * HTTP GET请求获取字节数据 - 对齐原版myssh实现
     */
    private static byte[] httpGetBytes(String urlStr) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestProperty("User-Agent", "FinalShell/3.8.3");
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("HTTP响应码: " + responseCode);
            }
            
            try (java.io.InputStream is = conn.getInputStream();
                 java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                return baos.toByteArray();
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private static String httpGet(String urlStr) throws IOException {
        byte[] data = httpGetBytes(urlStr);
        return new String(data, StandardCharsets.UTF_8);
    }
    
}
