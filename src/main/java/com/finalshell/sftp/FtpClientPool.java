package com.finalshell.sftp;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FTP客户端连接池
 * 管理FTP/SFTP客户端连接复用
 */
public class FtpClientPool {
    
    private static FtpClientPool instance;
    private Map<String, Queue<FtpClient>> pool = new ConcurrentHashMap<>();
    private int maxPoolSize = 5;
    private long connectionTimeout = 30000;
    
    private FtpClientPool() {}
    
    public static synchronized FtpClientPool getInstance() {
        if (instance == null) {
            instance = new FtpClientPool();
        }
        return instance;
    }
    
    public FtpClient borrowClient(String key) {
        Queue<FtpClient> clients = pool.get(key);
        if (clients != null && !clients.isEmpty()) {
            FtpClient client = clients.poll();
            if (client != null && client.isConnected()) {
                return client;
            }
        }
        return null;
    }
    
    public void returnClient(String key, FtpClient client) {
        if (client == null || !client.isConnected()) {
            return;
        }
        
        Queue<FtpClient> clients = pool.computeIfAbsent(key, k -> new LinkedList<>());
        if (clients.size() < maxPoolSize) {
            clients.offer(client);
        } else {
            client.disconnect();
        }
    }
    
    public void removeClient(String key) {
        Queue<FtpClient> clients = pool.remove(key);
        if (clients != null) {
            while (!clients.isEmpty()) {
                FtpClient client = clients.poll();
                if (client != null) {
                    client.disconnect();
                }
            }
        }
    }
    
    public void clear() {
        for (Queue<FtpClient> clients : pool.values()) {
            while (!clients.isEmpty()) {
                FtpClient client = clients.poll();
                if (client != null) {
                    client.disconnect();
                }
            }
        }
        pool.clear();
    }
    
    public void setMaxPoolSize(int size) {
        this.maxPoolSize = size;
    }
    
    public void setConnectionTimeout(long timeout) {
        this.connectionTimeout = timeout;
    }
    
    public int getPoolSize(String key) {
        Queue<FtpClient> clients = pool.get(key);
        return clients != null ? clients.size() : 0;
    }
}
