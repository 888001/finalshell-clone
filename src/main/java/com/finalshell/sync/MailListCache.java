package com.finalshell.sync;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件列表缓存
 * 用于缓存同步相关的邮件/消息列表
 */
public class MailListCache {
    
    private static MailListCache instance;
    private Map<String, List<SyncMessage>> cache = new ConcurrentHashMap<>();
    private long cacheTimeout = 5 * 60 * 1000; // 5分钟超时
    private Map<String, Long> lastUpdateTime = new ConcurrentHashMap<>();
    
    private MailListCache() {}
    
    public static synchronized MailListCache getInstance() {
        if (instance == null) {
            instance = new MailListCache();
        }
        return instance;
    }
    
    public void put(String key, List<SyncMessage> messages) {
        cache.put(key, new ArrayList<>(messages));
        lastUpdateTime.put(key, System.currentTimeMillis());
    }
    
    public List<SyncMessage> get(String key) {
        if (isExpired(key)) {
            cache.remove(key);
            lastUpdateTime.remove(key);
            return null;
        }
        List<SyncMessage> list = cache.get(key);
        return list != null ? new ArrayList<>(list) : null;
    }
    
    public boolean contains(String key) {
        return cache.containsKey(key) && !isExpired(key);
    }
    
    public void remove(String key) {
        cache.remove(key);
        lastUpdateTime.remove(key);
    }
    
    public void clear() {
        cache.clear();
        lastUpdateTime.clear();
    }
    
    public void setCacheTimeout(long timeout) {
        this.cacheTimeout = timeout;
    }
    
    private boolean isExpired(String key) {
        Long time = lastUpdateTime.get(key);
        if (time == null) return true;
        return System.currentTimeMillis() - time > cacheTimeout;
    }
    
    public static class SyncMessage {
        private String id;
        private String subject;
        private String content;
        private long timestamp;
        private boolean read;
        
        public SyncMessage() {}
        
        public SyncMessage(String id, String subject, String content) {
            this.id = id;
            this.subject = subject;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
        public boolean isRead() { return read; }
        public void setRead(boolean read) { this.read = read; }
    }
}
