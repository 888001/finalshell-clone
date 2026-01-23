package com.finalshell.ssh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSH连接池
 * 管理和复用SSH连接
 */
public class SshPool {
    
    private static SshPool instance;
    private Map<String, SSHSession> pool;
    private int maxPoolSize;
    private long idleTimeout;
    
    private SshPool() {
        this.pool = new ConcurrentHashMap<>();
        this.maxPoolSize = 10;
        this.idleTimeout = 30 * 60 * 1000;
    }
    
    public static synchronized SshPool getInstance() {
        if (instance == null) {
            instance = new SshPool();
        }
        return instance;
    }
    
    public SSHSession getSession(String key) {
        SSHSession session = pool.get(key);
        if (session != null && session.isConnected()) {
            return session;
        }
        if (session != null) {
            pool.remove(key);
        }
        return null;
    }
    
    public void putSession(String key, SSHSession session) {
        if (pool.size() >= maxPoolSize) {
            cleanupIdleSessions();
        }
        if (pool.size() < maxPoolSize) {
            pool.put(key, session);
        }
    }
    
    public void removeSession(String key) {
        SSHSession session = pool.remove(key);
        if (session != null) {
            session.disconnect();
        }
    }
    
    public void cleanupIdleSessions() {
        long now = System.currentTimeMillis();
        pool.entrySet().removeIf(entry -> {
            SSHSession session = entry.getValue();
            if (!session.isConnected()) {
                return true;
            }
            return false;
        });
    }
    
    public void closeAll() {
        for (SSHSession session : pool.values()) {
            try {
                session.disconnect();
            } catch (Exception e) {
            }
        }
        pool.clear();
    }
    
    public int getPoolSize() {
        return pool.size();
    }
    
    public int getMaxPoolSize() {
        return maxPoolSize;
    }
    
    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
    
    public long getIdleTimeout() {
        return idleTimeout;
    }
    
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
    
    public static String createKey(String host, int port, String username) {
        return username + "@" + host + ":" + port;
    }
}
