package com.finalshell.forward;

import com.finalshell.config.PortForwardConfig;
import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Port Forward Manager - Manages SSH port forwarding rules
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - SSH Port Forwarding
 */
public class PortForwardManager {
    
    private static final Logger logger = LoggerFactory.getLogger(PortForwardManager.class);
    
    private final SSHSession sshSession;
    private final Map<String, ForwardEntry> activeForwards = new LinkedHashMap<>();
    private final List<ForwardListener> listeners = new CopyOnWriteArrayList<>();
    
    public PortForwardManager(SSHSession sshSession) {
        this.sshSession = sshSession;
    }
    
    /**
     * Add local port forwarding
     * localhost:localPort -> remoteHost:remotePort
     */
    public ForwardEntry addLocalForward(int localPort, String remoteHost, int remotePort) throws SSHException {
        String id = "L:" + localPort + ":" + remoteHost + ":" + remotePort;
        
        if (activeForwards.containsKey(id)) {
            throw new SSHException("Forward already exists: " + id);
        }
        
        sshSession.addLocalPortForwarding(localPort, remoteHost, remotePort);
        
        ForwardEntry entry = new ForwardEntry(
            id,
            ForwardType.LOCAL,
            localPort,
            remoteHost,
            remotePort
        );
        entry.setStatus(ForwardStatus.ACTIVE);
        
        activeForwards.put(id, entry);
        fireEvent(ForwardEvent.ADDED, entry);
        
        logger.info("Local forward added: localhost:{} -> {}:{}", localPort, remoteHost, remotePort);
        return entry;
    }
    
    /**
     * Add remote port forwarding
     * remoteHost:remotePort -> localhost:localPort
     */
    public ForwardEntry addRemoteForward(int remotePort, String localHost, int localPort) throws SSHException {
        String id = "R:" + remotePort + ":" + localHost + ":" + localPort;
        
        if (activeForwards.containsKey(id)) {
            throw new SSHException("Forward already exists: " + id);
        }
        
        sshSession.addRemotePortForwarding(remotePort, localHost, localPort);
        
        ForwardEntry entry = new ForwardEntry(
            id,
            ForwardType.REMOTE,
            localPort,
            localHost,
            remotePort
        );
        entry.setStatus(ForwardStatus.ACTIVE);
        
        activeForwards.put(id, entry);
        fireEvent(ForwardEvent.ADDED, entry);
        
        logger.info("Remote forward added: remote:{} -> {}:{}", remotePort, localHost, localPort);
        return entry;
    }
    
    /**
     * Add dynamic port forwarding (SOCKS proxy)
     */
    public ForwardEntry addDynamicForward(int localPort) throws SSHException {
        String id = "D:" + localPort;
        
        if (activeForwards.containsKey(id)) {
            throw new SSHException("Forward already exists: " + id);
        }
        
        sshSession.addDynamicPortForwarding(localPort);
        
        ForwardEntry entry = new ForwardEntry(
            id,
            ForwardType.DYNAMIC,
            localPort,
            "SOCKS",
            0
        );
        entry.setStatus(ForwardStatus.ACTIVE);
        
        activeForwards.put(id, entry);
        fireEvent(ForwardEvent.ADDED, entry);
        
        logger.info("Dynamic forward (SOCKS) added: localhost:{}", localPort);
        return entry;
    }
    
    /**
     * Add forward from config
     */
    public ForwardEntry addForward(PortForwardConfig config) throws SSHException {
        switch (config.getType()) {
            case "local":
                return addLocalForward(config.getLocalPort(), config.getRemoteHost(), config.getRemotePort());
            case "remote":
                return addRemoteForward(config.getRemotePort(), config.getLocalHost(), config.getLocalPort());
            case "dynamic":
                return addDynamicForward(config.getLocalPort());
            default:
                throw new SSHException("Unknown forward type: " + config.getType());
        }
    }
    
    /**
     * Remove port forwarding
     */
    public void removeForward(String id) throws SSHException {
        ForwardEntry entry = activeForwards.get(id);
        if (entry == null) {
            return;
        }
        
        try {
            switch (entry.getType()) {
                case LOCAL:
                    sshSession.removeLocalPortForwarding(entry.getLocalPort());
                    break;
                case REMOTE:
                    sshSession.removeRemotePortForwarding(entry.getRemotePort());
                    break;
                case DYNAMIC:
                    sshSession.removeDynamicPortForwarding(entry.getLocalPort());
                    break;
            }
        } catch (Exception e) {
            logger.warn("Error removing forward: {}", e.getMessage());
        }
        
        entry.setStatus(ForwardStatus.STOPPED);
        activeForwards.remove(id);
        fireEvent(ForwardEvent.REMOVED, entry);
        
        logger.info("Forward removed: {}", id);
    }
    
    /**
     * Remove all forwards
     */
    public void removeAllForwards() {
        for (String id : new ArrayList<>(activeForwards.keySet())) {
            try {
                removeForward(id);
            } catch (Exception e) {
                logger.error("Error removing forward: {}", id, e);
            }
        }
    }
    
    /**
     * Get all active forwards
     */
    public List<ForwardEntry> getActiveForwards() {
        return new ArrayList<>(activeForwards.values());
    }
    
    /**
     * Get forward by ID
     */
    public ForwardEntry getForward(String id) {
        return activeForwards.get(id);
    }
    
    // Listeners
    public void addListener(ForwardListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(ForwardListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(ForwardEvent event, ForwardEntry entry) {
        for (ForwardListener listener : listeners) {
            try {
                listener.onForwardEvent(event, entry);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    /**
     * Forward Type
     */
    public enum ForwardType {
        LOCAL("本地转发"),
        REMOTE("远程转发"),
        DYNAMIC("动态转发");
        
        private final String displayName;
        
        ForwardType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Forward Status
     */
    public enum ForwardStatus {
        ACTIVE("运行中"),
        STOPPED("已停止"),
        ERROR("错误");
        
        private final String displayName;
        
        ForwardStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Forward Entry
     */
    public static class ForwardEntry {
        private final String id;
        private final ForwardType type;
        private final int localPort;
        private final String remoteHost;
        private final int remotePort;
        private ForwardStatus status = ForwardStatus.STOPPED;
        private String error;
        private long createdTime;
        
        public ForwardEntry(String id, ForwardType type, int localPort, String remoteHost, int remotePort) {
            this.id = id;
            this.type = type;
            this.localPort = localPort;
            this.remoteHost = remoteHost;
            this.remotePort = remotePort;
            this.createdTime = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public ForwardType getType() { return type; }
        public int getLocalPort() { return localPort; }
        public String getRemoteHost() { return remoteHost; }
        public int getRemotePort() { return remotePort; }
        
        public ForwardStatus getStatus() { return status; }
        public void setStatus(ForwardStatus status) { this.status = status; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public long getCreatedTime() { return createdTime; }
        
        public String getDescription() {
            switch (type) {
                case LOCAL:
                    return String.format("localhost:%d → %s:%d", localPort, remoteHost, remotePort);
                case REMOTE:
                    return String.format("remote:%d → %s:%d", remotePort, remoteHost, localPort);
                case DYNAMIC:
                    return String.format("SOCKS localhost:%d", localPort);
                default:
                    return id;
            }
        }
    }
    
    /**
     * Forward Event
     */
    public enum ForwardEvent {
        ADDED,
        REMOVED,
        STATUS_CHANGED,
        ERROR
    }
    
    /**
     * Forward Listener
     */
    public interface ForwardListener {
        void onForwardEvent(ForwardEvent event, ForwardEntry entry);
    }
}
