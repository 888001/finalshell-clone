package com.finalshell.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 网络工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Utility_DeepAnalysis.md - Network Utils
 */
public class NetworkUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);
    
    /**
     * Ping测试
     */
    public static PingResult ping(String host, int timeout) {
        PingResult result = new PingResult();
        result.host = host;
        
        try {
            InetAddress address = InetAddress.getByName(host);
            long start = System.currentTimeMillis();
            boolean reachable = address.isReachable(timeout);
            long end = System.currentTimeMillis();
            
            result.success = reachable;
            result.time = end - start;
            result.ip = address.getHostAddress();
        } catch (Exception e) {
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * 端口检测
     */
    public static boolean isPortOpen(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取本机所有IP地址
     */
    public static List<String> getLocalIPs() {
        List<String> ips = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) continue;
                
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        ips.add(addr.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            logger.warn("获取本机IP失败", e);
        }
        return ips;
    }
    
    /**
     * 获取外网IP
     */
    public static String getPublicIP() {
        String[] services = {
            "http://checkip.amazonaws.com",
            "http://icanhazip.com",
            "http://ipecho.net/plain"
        };
        
        for (String service : services) {
            try {
                String ip = HttpTools.get(service).trim();
                if (isValidIP(ip)) {
                    return ip;
                }
            } catch (Exception e) {
                // 尝试下一个服务
            }
        }
        return null;
    }
    
    /**
     * 验证IP地址格式
     */
    public static boolean isValidIP(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 解析主机名
     */
    public static String resolveHost(String hostname) {
        try {
            InetAddress address = InetAddress.getByName(hostname);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }
    
    /**
     * Ping结果
     */
    public static class PingResult {
        public String host;
        public String ip;
        public boolean success;
        public long time;
        public String error;
        
        @Override
        public String toString() {
            if (success) {
                return String.format("Ping %s (%s): %d ms", host, ip, time);
            } else {
                return String.format("Ping %s: failed (%s)", host, error);
            }
        }
    }
}
