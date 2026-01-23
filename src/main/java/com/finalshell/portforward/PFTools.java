package com.finalshell.portforward;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 端口转发工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFTools {
    
    public static boolean isPortAvailable(int port) {
        try (ServerSocket socket = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public static int findAvailablePort(int startPort, int endPort) {
        for (int port = startPort; port <= endPort; port++) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        return -1;
    }
    
    public static String formatRule(PFRule rule) {
        if (rule == null) return "";
        
        StringBuilder sb = new StringBuilder();
        sb.append(rule.getType()).append(": ");
        sb.append(rule.getLocalHost()).append(":").append(rule.getLocalPort());
        sb.append(" -> ");
        sb.append(rule.getRemoteHost()).append(":").append(rule.getRemotePort());
        return sb.toString();
    }
    
    public static boolean validateHost(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }
        // 简单验证
        return host.matches("^[a-zA-Z0-9.-]+$");
    }
    
    public static boolean validatePort(int port) {
        return port > 0 && port <= 65535;
    }
    
    public static boolean validateRule(PFRule rule) {
        if (rule == null) return false;
        
        if (!validateHost(rule.getLocalHost())) return false;
        if (!validatePort(rule.getLocalPort())) return false;
        if (!validateHost(rule.getRemoteHost())) return false;
        if (!validatePort(rule.getRemotePort())) return false;
        
        return true;
    }
    
    public static void forwardData(Socket source, Socket target) throws IOException {
        Thread t1 = new Thread(() -> {
            try {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = source.getInputStream().read(buffer)) != -1) {
                    target.getOutputStream().write(buffer, 0, len);
                    target.getOutputStream().flush();
                }
            } catch (IOException e) {
                // 连接关闭
            }
        });
        
        Thread t2 = new Thread(() -> {
            try {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = target.getInputStream().read(buffer)) != -1) {
                    source.getOutputStream().write(buffer, 0, len);
                    source.getOutputStream().flush();
                }
            } catch (IOException e) {
                // 连接关闭
            }
        });
        
        t1.start();
        t2.start();
    }
}
