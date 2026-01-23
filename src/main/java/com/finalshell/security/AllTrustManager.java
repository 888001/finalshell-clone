package com.finalshell.security;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * 信任所有证书的管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: BatchClasses_Analysis.md - AllTrustManager
 * 
 * WARNING: 此类仅用于测试或内网环境，不应在生产环境使用
 */
public class AllTrustManager implements X509TrustManager {
    
    private static SSLContext sslContext;
    private static SSLSocketFactory sslSocketFactory;
    private static HostnameVerifier hostnameVerifier;
    
    static {
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new AllTrustManager()}, 
                new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
            hostnameVerifier = (hostname, session) -> true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // 信任所有客户端证书
    }
    
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        // 信任所有服务器证书
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
    
    public static SSLContext getSSLContext() {
        return sslContext;
    }
    
    public static SSLSocketFactory getSSLSocketFactory() {
        return sslSocketFactory;
    }
    
    public static HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }
    
    public static void applyToHttpsConnection(javax.net.ssl.HttpsURLConnection conn) {
        conn.setSSLSocketFactory(sslSocketFactory);
        conn.setHostnameVerifier(hostnameVerifier);
    }
}
