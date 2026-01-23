package com.finalshell.forward;

import java.io.*;
import java.net.*;

/**
 * 转发工具类
 * 提供端口转发相关的工具方法
 */
public class FwTools {
    
    private static final int BUFFER_SIZE = 8192;
    
    public static void forward(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
            out.flush();
        }
    }
    
    public static Thread startForwardThread(InputStream in, OutputStream out, Runnable onComplete) {
        Thread thread = new Thread(() -> {
            try {
                forward(in, out);
            } catch (IOException e) {
            } finally {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
    
    public static void bidirectionalForward(Socket src, Socket dest) throws IOException {
        Thread t1 = startForwardThread(src.getInputStream(), dest.getOutputStream(), null);
        Thread t2 = startForwardThread(dest.getInputStream(), src.getOutputStream(), null);
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static boolean isPortAvailable(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            ss.setReuseAddress(true);
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
    
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
    
    public static boolean isValidHost(String host) {
        if (host == null || host.isEmpty()) return false;
        try {
            InetAddress.getByName(host);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    public static String formatRule(String type, int localPort, String remoteHost, int remotePort) {
        return String.format("%s: localhost:%d -> %s:%d", type, localPort, remoteHost, remotePort);
    }
}
