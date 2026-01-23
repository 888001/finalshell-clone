package com.finalshell.rdp;

import com.finalshell.ssh.SSHException;
import com.finalshell.ssh.SSHSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * RDP Session - Manages RDP connection via SSH tunnel
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - RDP over SSH
 */
public class RDPSession {
    
    private static final Logger logger = LoggerFactory.getLogger(RDPSession.class);
    
    private final RDPConfig config;
    private final SSHSession sshSession;
    
    private Process rdpProcess;
    private boolean connected = false;
    private boolean tunnelEstablished = false;
    
    private final List<RDPListener> listeners = new CopyOnWriteArrayList<>();
    
    public RDPSession(RDPConfig config, SSHSession sshSession) {
        this.config = config;
        this.sshSession = sshSession;
    }
    
    /**
     * Connect to RDP server
     */
    public void connect() throws RDPException {
        try {
            fireEvent(RDPEvent.CONNECTING);
            
            // Setup SSH tunnel if needed
            if (config.isUseSshTunnel()) {
                setupTunnel();
            }
            
            // Launch RDP client
            launchRDPClient();
            
            connected = true;
            fireEvent(RDPEvent.CONNECTED);
            
        } catch (Exception e) {
            fireEvent(RDPEvent.ERROR, e.getMessage());
            throw new RDPException("RDP connection failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup SSH tunnel for RDP
     */
    private void setupTunnel() throws SSHException {
        logger.info("Setting up SSH tunnel for RDP: localhost:{} -> {}:{}", 
            config.getLocalTunnelPort(), config.getRdpHost(), config.getRdpPort());
        
        sshSession.addLocalPortForwarding(
            config.getLocalTunnelPort(),
            config.getRdpHost(),
            config.getRdpPort()
        );
        
        tunnelEstablished = true;
        logger.info("SSH tunnel established");
    }
    
    /**
     * Launch RDP client (platform-specific)
     */
    private void launchRDPClient() throws RDPException {
        String os = System.getProperty("os.name").toLowerCase();
        
        try {
            if (os.contains("win")) {
                launchMstsc();
            } else if (os.contains("mac")) {
                launchMacRDP();
            } else {
                launchFreeRDP();
            }
        } catch (IOException e) {
            throw new RDPException("Failed to launch RDP client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Launch Windows mstsc.exe
     */
    private void launchMstsc() throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add("mstsc.exe");
        cmd.add("/v:" + config.getEffectiveHost() + ":" + config.getEffectivePort());
        
        if (config.getWidth() > 0 && config.getHeight() > 0) {
            cmd.add("/w:" + config.getWidth());
            cmd.add("/h:" + config.getHeight());
        }
        
        if (config.isFullscreen()) {
            cmd.add("/f");
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmd);
        rdpProcess = pb.start();
        
        logger.info("Launched mstsc: {}", String.join(" ", cmd));
    }
    
    /**
     * Launch macOS Microsoft Remote Desktop
     */
    private void launchMacRDP() throws IOException {
        // Use open command to launch RDP URL
        String rdpUrl = String.format("rdp://full%%20address=s:%s:%d", 
            config.getEffectiveHost(), config.getEffectivePort());
        
        ProcessBuilder pb = new ProcessBuilder("open", rdpUrl);
        rdpProcess = pb.start();
        
        logger.info("Launched macOS RDP: {}", rdpUrl);
    }
    
    /**
     * Launch FreeRDP (xfreerdp) on Linux
     */
    private void launchFreeRDP() throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add("xfreerdp");
        cmd.add("/v:" + config.getEffectiveHost() + ":" + config.getEffectivePort());
        cmd.add("/size:" + config.getWidth() + "x" + config.getHeight());
        
        if (config.getRdpUsername() != null && !config.getRdpUsername().isEmpty()) {
            cmd.add("/u:" + config.getRdpUsername());
        }
        
        if (config.getRdpDomain() != null && !config.getRdpDomain().isEmpty()) {
            cmd.add("/d:" + config.getRdpDomain());
        }
        
        if (config.isFullscreen()) {
            cmd.add("/f");
        }
        
        if (config.isEnableClipboard()) {
            cmd.add("+clipboard");
        }
        
        if (!config.isEnableNLA()) {
            cmd.add("/cert-ignore");
            cmd.add("-sec-nla");
        }
        
        ProcessBuilder pb = new ProcessBuilder(cmd);
        rdpProcess = pb.start();
        
        logger.info("Launched xfreerdp: {}", String.join(" ", cmd));
    }
    
    /**
     * Disconnect RDP session
     */
    public void disconnect() {
        if (rdpProcess != null && rdpProcess.isAlive()) {
            rdpProcess.destroy();
            logger.info("RDP process terminated");
        }
        
        if (tunnelEstablished && sshSession != null) {
            try {
                sshSession.removeLocalPortForwarding(config.getLocalTunnelPort());
                tunnelEstablished = false;
            } catch (Exception e) {
                logger.warn("Failed to remove SSH tunnel: {}", e.getMessage());
            }
        }
        
        connected = false;
        fireEvent(RDPEvent.DISCONNECTED);
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected && (rdpProcess == null || rdpProcess.isAlive());
    }
    
    // Listeners
    public void addListener(RDPListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(RDPListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(RDPEvent event) {
        fireEvent(event, null);
    }
    
    private void fireEvent(RDPEvent event, String message) {
        for (RDPListener listener : listeners) {
            try {
                listener.onRDPEvent(event, message);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    /**
     * RDP Event
     */
    public enum RDPEvent {
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        ERROR
    }
    
    /**
     * RDP Listener
     */
    public interface RDPListener {
        void onRDPEvent(RDPEvent event, String message);
    }
}
