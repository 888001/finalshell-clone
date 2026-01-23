package com.finalshell.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.security.MessageDigest;
import java.util.Enumeration;

/**
 * 设备工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Utility_DeepAnalysis.md - Tools.java 网络工具方法
 */
public class DeviceUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceUtils.class);
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    
    private static String cachedDeviceId;
    
    /**
     * 获取设备ID (基于MAC地址哈希)
     */
    public static synchronized String getDeviceId() {
        if (cachedDeviceId != null) {
            return cachedDeviceId;
        }
        
        try {
            StringBuilder builder = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte b : mac) {
                        builder.append(Integer.toHexString(b & 0xFF));
                    }
                }
            }
            
            cachedDeviceId = md5(builder.toString());
        } catch (Exception e) {
            logger.warn("获取设备ID失败", e);
            cachedDeviceId = md5("unknown-device-" + System.currentTimeMillis());
        }
        
        return cachedDeviceId;
    }
    
    /**
     * 获取本机IP地址
     */
    public static String getLocalIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.warn("获取本机IP失败", e);
        }
        return "127.0.0.1";
    }
    
    /**
     * 获取主机名
     */
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }
    
    /**
     * 获取MAC地址
     */
    public static String getMACAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X", mac[i]));
                        if (i < mac.length - 1) sb.append(":");
                    }
                    return sb.toString();
                }
            }
        } catch (SocketException e) {
            logger.warn("获取MAC地址失败", e);
        }
        return "00:00:00:00:00:00";
    }
    
    /**
     * 获取操作系统名称
     */
    public static String getOSName() {
        return System.getProperty("os.name", "unknown");
    }
    
    /**
     * 获取操作系统版本
     */
    public static String getOSVersion() {
        return System.getProperty("os.version", "unknown");
    }
    
    /**
     * 获取Java版本
     */
    public static String getJavaVersion() {
        return System.getProperty("java.version", "unknown");
    }
    
    /**
     * 获取用户名
     */
    public static String getUserName() {
        return System.getProperty("user.name", "unknown");
    }
    
    /**
     * 获取用户主目录
     */
    public static String getUserHome() {
        return System.getProperty("user.home", ".");
    }
    
    /**
     * MD5哈希
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            
            char[] hexString = new char[32];
            for (int i = 0; i < 16; i++) {
                hexString[i * 2] = HEX_CHARS[(digest[i] >> 4) & 0xF];
                hexString[i * 2 + 1] = HEX_CHARS[digest[i] & 0xF];
            }
            return new String(hexString);
        } catch (Exception e) {
            return input.hashCode() + "";
        }
    }
}
