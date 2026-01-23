package com.finalshell.forward;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义加速管理器
 * 管理端口映射规则和加速配置
 */
public class CustomAccManager {
    
    private static CustomAccManager instance;
    private Map<String, MapRule> rules;
    private Map<String, ForwardSession> sessions;
    private boolean enabled;
    
    private CustomAccManager() {
        this.rules = new ConcurrentHashMap<>();
        this.sessions = new ConcurrentHashMap<>();
        this.enabled = true;
    }
    
    public static synchronized CustomAccManager getInstance() {
        if (instance == null) {
            instance = new CustomAccManager();
        }
        return instance;
    }
    
    public void addRule(MapRule rule) {
        rules.put(rule.getId(), rule);
    }
    
    public void removeRule(String id) {
        rules.remove(id);
        stopSession(id);
    }
    
    public MapRule getRule(String id) {
        return rules.get(id);
    }
    
    public List<MapRule> getRules() {
        return new ArrayList<>(rules.values());
    }
    
    public void startSession(String ruleId) {
        MapRule rule = rules.get(ruleId);
        if (rule == null) return;
        
        ForwardSession session = new ForwardSession(rule);
        sessions.put(ruleId, session);
        session.start();
    }
    
    public void stopSession(String ruleId) {
        ForwardSession session = sessions.remove(ruleId);
        if (session != null) {
            session.stop();
        }
    }
    
    public void stopAllSessions() {
        for (ForwardSession session : sessions.values()) {
            session.stop();
        }
        sessions.clear();
    }
    
    public boolean isSessionRunning(String ruleId) {
        ForwardSession session = sessions.get(ruleId);
        return session != null && session.isRunning();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopAllSessions();
        }
    }
    
    public int getActiveSessionCount() {
        int count = 0;
        for (ForwardSession session : sessions.values()) {
            if (session.isRunning()) count++;
        }
        return count;
    }
    
    private static class ForwardSession {
        private MapRule rule;
        private boolean running;
        
        ForwardSession(MapRule rule) {
            this.rule = rule;
        }
        
        void start() {
            running = true;
        }
        
        void stop() {
            running = false;
        }
        
        boolean isRunning() {
            return running;
        }
    }
}
