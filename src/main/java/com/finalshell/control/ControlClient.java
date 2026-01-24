package com.finalshell.control;

import com.alibaba.fastjson.JSONObject;
import com.finalshell.util.DeviceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Random;

/**
 * 控制客户端 - 用户认证和授权
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Control_Auth_DeepAnalysis.md - ControlClient
 */
public class ControlClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ControlClient.class);
    
    private static ControlClient instance;
    
    private Thread loginThread;
    private volatile boolean isRunning = false;
    private String deviceId;
    private int loginCode = -1;
    private final Object syncLock = new Object();
    private long lastActiveTime = 0L;
    
    private String serverHost = "localhost";
    private int serverPort = 8080;
    private int maxRetries = 3;
    
    private boolean isPro = false;
    private boolean isPermanent = false;
    private volatile boolean isLoginComplete = false;
    
    private ControlClient() {
        this.deviceId = DeviceUtils.getDeviceId();
    }
    
    public static synchronized ControlClient getInstance() {
        if (instance == null) {
            instance = new ControlClient();
        }
        return instance;
    }
    
    /**
     * 登录
     */
    public void login(String username, String password, LoginCallback callback) {
        if (isRunning) {
            return;
        }
        
        loginThread = new Thread(() -> {
            isRunning = true;
            try {
                boolean success = doLogin(username, password);
                if (callback != null) {
                    callback.onLoginComplete(success, loginCode, isPro);
                }
            } catch (Exception e) {
                logger.error("登录失败", e);
                if (callback != null) {
                    callback.onLoginComplete(false, -1, false);
                }
            } finally {
                isRunning = false;
            }
        }, "LoginThread");
        loginThread.start();
    }
    
    private boolean doLogin(String username, String password) {
        isLoginComplete = false;
        isPro = false;
        loginCode = 0;
        
        try {
            // 模拟登录请求
            JSONObject request = new JSONObject();
            request.put("command", "login");
            request.put("username", username);
            request.put("password", password);
            request.put("device_id", deviceId);
            
            // TODO: 实际HTTP请求
            // JSONObject response = HttpTools.postRequest(request, loginUrl);
            
            // 模拟响应
            loginCode = 0; // 成功
            isPro = false;
            isPermanent = false;
            
            isLoginComplete = true;
            lastActiveTime = System.currentTimeMillis();
            
            return loginCode == 0;
        } catch (Exception e) {
            logger.error("登录请求失败", e);
            return false;
        }
    }
    
    /**
     * 检查授权状态
     */
    public void checkLicense(LicenseCallback callback) {
        new Thread(() -> {
            try {
                // 模拟检查
                boolean valid = isPro && (isPermanent || !isExpired());
                if (callback != null) {
                    callback.onLicenseChecked(valid, isPermanent);
                }
            } catch (Exception e) {
                logger.error("检查授权失败", e);
                if (callback != null) {
                    callback.onLicenseChecked(false, false);
                }
            }
        }).start();
    }
    
    private boolean isExpired() {
        // 检查过期逻辑
        return false;
    }
    
    /**
     * 注销
     */
    public void logout() {
        isPro = false;
        isPermanent = false;
        isLoginComplete = false;
        loginCode = -1;
    }
    
    /**
     * 创建SSL上下文
     */
    public static SSLContext createSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext;
    }
    
    // Getters
    public boolean isPro() { return isPro; }
    public boolean isPermanent() { return isPermanent; }
    public boolean isLoginComplete() { return isLoginComplete; }
    public int getLoginCode() { return loginCode; }
    public String getDeviceId() { return deviceId; }
    
    public boolean isConnected() { return isLoginComplete; }
    
    public void checkStatus() {
        // Check status periodically
        if (isLoginComplete) {
            lastActiveTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 登录回调接口
     */
    public interface LoginCallback {
        void onLoginComplete(boolean success, int code, boolean isPro);
    }
    
    /**
     * 授权回调接口
     */
    public interface LicenseCallback {
        void onLicenseChecked(boolean valid, boolean isPermanent);
    }
}
