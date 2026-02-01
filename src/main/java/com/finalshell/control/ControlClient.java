package com.finalshell.control;

import com.alibaba.fastjson.JSONObject;
import com.finalshell.util.DeviceUtils;
import com.finalshell.util.DesUtilPro;
import com.finalshell.update.UpdateChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private volatile long lastLicenseCheckTime = 0L;
    private long licenseCheckIntervalMs = 10 * 60 * 1000L;
    
    private String serverHost = "localhost";
    private int serverPort = 8080;
    private int maxRetries = 3;
    
    private boolean isPro = false;
    private boolean isPermanent = false;
    private volatile boolean isLoginComplete = false;
    private volatile String message = "";
    private volatile long expireTime = -1L;
    private volatile SetProListener setProListener;

    private volatile String lastUsername;
    private volatile String lastPassword;
    
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
                lastUsername = username;
                lastPassword = password;
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
            JSONObject request = new JSONObject();
            request.put("ver_num", UpdateChecker.CURRENT_VERSION);
            request.put("command", "login");
            request.put("username", username);
            request.put("password", password);
            request.put("device_id", deviceId);

            JSONObject response = postEncryptedJson(request);

            if (response == null) {
                loginCode = -1;
                message = "登录失败";
                LoginDialog.message = message;
                return false;
            }

            loginCode = response.getIntValue("code");
            message = response.getString("msg");
            LoginDialog.message = message != null ? message : "";
            isPro = response.getBooleanValue("pro");
            isPermanent = response.getBooleanValue("pf") || response.getBooleanValue("permanent");

            expireTime = parseExpireTime(response);

            isLoginComplete = (loginCode == 1);
            if (isLoginComplete) {
                lastActiveTime = System.currentTimeMillis();
            }

            notifyProStatus();

            return isLoginComplete;
        } catch (Exception e) {
            logger.error("登录请求失败", e);
            loginCode = -1;
            message = "登录失败";
            LoginDialog.message = message;
            return false;
        }
    }
    
    /**
     * 检查授权状态
     */
    public void checkLicense(LicenseCallback callback) {
        new Thread(() -> {
            try {
                boolean refreshed = false;
                String u = lastUsername;
                String p = lastPassword;
                if (u != null && !u.isEmpty() && p != null && !p.isEmpty()) {
                    refreshed = doLogin(u, p);
                }

                boolean valid = isPro && (isPermanent || !isExpired());
                notifyProStatus();
                if (callback != null) {
                    callback.onLicenseChecked(valid && (refreshed || isLoginComplete), isPermanent);
                }
            } catch (Exception e) {
                logger.error("检查授权失败", e);
                notifyProStatus();
                if (callback != null) {
                    callback.onLicenseChecked(false, false);
                }
            }
        }).start();
    }
    
    private boolean isExpired() {
        // 检查过期逻辑
        if (isPermanent) return false;
        long et = normalizeExpireTime(expireTime);
        if (et <= 0) return false;
        return System.currentTimeMillis() > et;
    }

    private void notifyProStatus() {
        SetProListener listener = this.setProListener;
        if (listener == null) {
            return;
        }
        boolean valid = isPro && (isPermanent || !isExpired());
        try {
            listener.setProStatus(isPro, valid);
        } catch (Exception e) {
            logger.warn("SetProListener callback failed", e);
        }
    }

    private long normalizeExpireTime(long et) {
        if (et <= 0) return et;
        if (et < 100000000000L) {
            return et * 1000L;
        }
        return et;
    }

    private long parseExpireTime(JSONObject response) {
        if (response == null) {
            return -1L;
        }

        Object v = response.get("expire_time");
        if (v == null) {
            v = response.get("expire");
        }
        if (v == null) {
            return -1L;
        }

        if (v instanceof Number) {
            return normalizeExpireTime(((Number) v).longValue());
        }

        String s = String.valueOf(v).trim();
        if (s.isEmpty() || "0".equals(s) || "-1".equals(s) || "null".equalsIgnoreCase(s)) {
            return -1L;
        }

        try {
            long n = Long.parseLong(s);
            return normalizeExpireTime(n);
        } catch (NumberFormatException ignored) {
        }

        long parsed = parseDateTimeMillis(s);
        return parsed > 0 ? parsed : -1L;
    }

    private long parseDateTimeMillis(String s) {
        ZoneId zone = ZoneId.systemDefault();
        for (String pattern : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy-MM-dd" , "yyyy/MM/dd"}) {
            try {
                if (pattern.contains("HH")) {
                    LocalDateTime dt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern(pattern));
                    return dt.atZone(zone).toInstant().toEpochMilli();
                }
                LocalDate d = LocalDate.parse(s, DateTimeFormatter.ofPattern(pattern));
                return d.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli() - 1L;
            } catch (DateTimeParseException ignored) {
            }
        }
        return -1L;
    }

    private String getServerUrl() {
        String url = System.getProperty("control.server.url");
        if (url != null && !url.trim().isEmpty()) {
            return url.trim();
        }
        return "https://" + serverHost + ":" + serverPort + "/";
    }

    private JSONObject postEncryptedJson(JSONObject request) throws Exception {
        Exception lastException = null;
        int attempts = Math.max(1, maxRetries);
        for (int i = 0; i < attempts; i++) {
            HttpURLConnection conn = null;
            try {
                String urlStr = getServerUrl();
                URL url = new URL(urlStr);
                URLConnection connection = url.openConnection();

                if (connection instanceof HttpsURLConnection) {
                    SSLContext sslContext = createSSLContext();
                    HttpsURLConnection https = (HttpsURLConnection) connection;
                    https.setSSLSocketFactory(sslContext.getSocketFactory());
                    https.setHostnameVerifier((hostname, session) -> true);
                }

                conn = (HttpURLConnection) connection;
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/octet-stream");

                byte[] requestData = request.toJSONString().getBytes("UTF-8");
                requestData = DesUtilPro.encryptBytes(requestData);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(requestData);
                }

                int code = conn.getResponseCode();
                if (code != 200) {
                    throw new IOException("HTTP Error: " + code);
                }

                byte[] responseData;
                try (InputStream is = conn.getInputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = is.read(buf)) > -1) {
                        bos.write(buf, 0, len);
                    }
                    responseData = bos.toByteArray();
                }

                responseData = DesUtilPro.decryptBytes(responseData);
                String text = new String(responseData, "UTF-8");
                return JSONObject.parseObject(text);
            } catch (Exception e) {
                lastException = e;
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ignored) {
                }
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        throw lastException != null ? lastException : new Exception("request failed");
    }
    
    /**
     * 注销
     */
    public void logout() {
        isPro = false;
        isPermanent = false;
        isLoginComplete = false;
        loginCode = -1;
        lastUsername = null;
        lastPassword = null;
        notifyProStatus();
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
    public String getMessage() { return message; }
    public long getExpireTimeMillis() { return normalizeExpireTime(expireTime); }
    public boolean isExpiredNow() { return isExpired(); }
    public boolean isProValid() { return isPro && (isPermanent || !isExpired()); }
    
    public boolean isConnected() { return isLoginComplete; }

    public void setSetProListener(SetProListener listener) {
        this.setProListener = listener;
        notifyProStatus();
    }
    
    public void checkStatus() {
        if (isLoginComplete) {
            lastActiveTime = System.currentTimeMillis();
            long now = lastActiveTime;
            if (now - lastLicenseCheckTime >= licenseCheckIntervalMs) {
                lastLicenseCheckTime = now;
                checkLicense(null);
            }
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
