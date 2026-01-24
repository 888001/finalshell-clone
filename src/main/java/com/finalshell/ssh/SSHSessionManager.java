package com.finalshell.ssh;

import com.finalshell.config.ConnectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SSH Session Manager - Manages multiple SSH sessions
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSHManager_DeepAnalysis.md
 */
public class SSHSessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SSHSessionManager.class);
    
    private static SSHSessionManager instance;
    
    private final Map<String, SSHSession> sessions = new ConcurrentHashMap<>();
    
    public static SSHSessionManager getInstance() {
        if (instance == null) {
            instance = new SSHSessionManager();
        }
        return instance;
    }
    
    private SSHSessionManager() {
    }
    
    /**
     * Create and connect a new SSH session
     */
    public SSHSession createSession(ConnectConfig config) throws SSHException {
        String sessionId = generateSessionId(config);
        
        SSHSession session = new SSHSession(config);
        sessions.put(sessionId, session);
        
        logger.info("Created session: {}", sessionId);
        return session;
    }
    
    /**
     * Get existing session by config
     */
    public SSHSession getSession(ConnectConfig config) {
        String sessionId = generateSessionId(config);
        return sessions.get(sessionId);
    }
    
    /**
     * Get session by ID
     */
    public SSHSession getSession(String sessionId) {
        return sessions.get(sessionId);
    }
    
    /**
     * Close session
     */
    public void closeSession(String sessionId) {
        SSHSession session = sessions.remove(sessionId);
        if (session != null) {
            session.disconnect();
            logger.info("Closed session: {}", sessionId);
        }
    }
    
    /**
     * Close session by config
     */
    public void closeSession(ConnectConfig config) {
        String sessionId = generateSessionId(config);
        closeSession(sessionId);
    }
    
    /**
     * Close all sessions
     */
    public void closeAllSessions() {
        for (String sessionId : sessions.keySet()) {
            closeSession(sessionId);
        }
        logger.info("All sessions closed");
    }
    
    /**
     * Get active session count
     */
    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
            .filter(SSHSession::isConnected)
            .count();
    }
    
    /**
     * Generate unique session ID
     */
    private String generateSessionId(ConnectConfig config) {
        return config.getId() + "_" + System.currentTimeMillis();
    }
    
    /**
     * Get all sessions
     */
    public Map<String, SSHSession> getAllSessions() {
        return sessions;
    }
    
    /**
     * Get active session (first connected session)
     */
    public SSHSession getActiveSession() {
        for (SSHSession session : sessions.values()) {
            if (session.isConnected()) {
                return session;
            }
        }
        return null;
    }
}
