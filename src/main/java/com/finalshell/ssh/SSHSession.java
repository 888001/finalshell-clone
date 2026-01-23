package com.finalshell.ssh;

import com.finalshell.config.ConnectConfig;
import com.finalshell.config.PortForwardConfig;
import com.finalshell.config.ProxyConfig;
import com.finalshell.util.EncryptUtil;
import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * SSH Session - JSch wrapper for SSH connections
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSHManager_DeepAnalysis.md, Network_Protocol_Analysis.md
 */
public class SSHSession {
    
    private static final Logger logger = LoggerFactory.getLogger(SSHSession.class);
    
    private final ConnectConfig config;
    private final List<SSHSessionListener> listeners = new CopyOnWriteArrayList<>();
    
    private JSch jsch;
    private Session session;
    private ChannelShell shellChannel;
    private ChannelSftp sftpChannel;
    
    private InputStream inputStream;
    private OutputStream outputStream;
    
    private boolean connected = false;
    private String lastError;
    
    // Port forward tracking
    private final Map<String, Integer> localForwards = new HashMap<>();
    private final Map<String, Integer> remoteForwards = new HashMap<>();
    
    public SSHSession(ConnectConfig config) {
        this.config = config;
        this.jsch = new JSch();
    }
    
    /**
     * Connect to SSH server
     */
    public void connect() throws SSHException {
        try {
            fireEvent(SSHEvent.CONNECTING);
            logger.info("Connecting to {}@{}:{}", config.getUserName(), config.getHost(), config.getPort());
            
            // Configure JSch
            configureJSch();
            
            // Create session
            session = jsch.getSession(config.getUserName(), config.getHost(), config.getPort());
            
            // Configure session
            configureSession();
            
            // Connect
            int timeout = config.getTimeout() > 0 ? config.getTimeout() : 30000;
            session.connect(timeout);
            
            connected = true;
            fireEvent(SSHEvent.CONNECTED);
            logger.info("Connected to {}:{}", config.getHost(), config.getPort());
            
            // Setup port forwards if configured
            setupPortForwards();
            
        } catch (JSchException e) {
            lastError = e.getMessage();
            fireEvent(SSHEvent.ERROR, e.getMessage());
            logger.error("SSH connection failed: {}", e.getMessage());
            throw new SSHException("Connection failed: " + e.getMessage(), e);
        }
    }
    
    private void configureJSch() throws JSchException {
        // Add private key if configured
        if (config.hasPrivateKey()) {
            String keyPath = config.getPrivateKey();
            String passphrase = config.getPassphrase();
            
            if (passphrase != null && !passphrase.isEmpty()) {
                jsch.addIdentity(keyPath, passphrase);
            } else {
                jsch.addIdentity(keyPath);
            }
            logger.debug("Added private key: {}", keyPath);
        }
        
        // Known hosts (disable strict checking for now)
        JSch.setConfig("StrictHostKeyChecking", "no");
    }
    
    private void configureSession() throws JSchException {
        // Proxy configuration
        configureProxy();
        
        // Password authentication
        String password = config.getPassword();
        if (password != null && !password.isEmpty()) {
            // Decrypt if encrypted
            if (EncryptUtil.isEncrypted(password)) {
                password = EncryptUtil.decryptDES(password);
            }
            session.setPassword(password);
        }
        
        // Session configuration
        Properties sessionConfig = new Properties();
        sessionConfig.put("StrictHostKeyChecking", "no");
        sessionConfig.put("PreferredAuthentications", "publickey,keyboard-interactive,password");
        
        // Compression
        if (config.isEnableCompression()) {
            sessionConfig.put("compression.s2c", "zlib@openssh.com,zlib,none");
            sessionConfig.put("compression.c2s", "zlib@openssh.com,zlib,none");
        }
        
        // Keep alive
        if (config.getKeepAliveInterval() > 0) {
            session.setServerAliveInterval(config.getKeepAliveInterval() * 1000);
            session.setServerAliveCountMax(3);
        }
        
        session.setConfig(sessionConfig);
    }
    
    /**
     * Configure proxy settings
     */
    private void configureProxy() throws JSchException {
        ProxyConfig proxy = config.getProxyConfig();
        if (proxy == null || !proxy.isEnabled()) {
            return;
        }
        
        switch (proxy.getType()) {
            case HTTP:
                com.jcraft.jsch.ProxyHTTP httpProxy = new com.jcraft.jsch.ProxyHTTP(
                    proxy.getProxyHost(), proxy.getProxyPort());
                if (proxy.getProxyUsername() != null && !proxy.getProxyUsername().isEmpty()) {
                    httpProxy.setUserPasswd(proxy.getProxyUsername(), proxy.getProxyPassword());
                }
                session.setProxy(httpProxy);
                logger.info("Using HTTP proxy: {}:{}", proxy.getProxyHost(), proxy.getProxyPort());
                break;
                
            case SOCKS4:
                com.jcraft.jsch.ProxySOCKS4 socks4Proxy = new com.jcraft.jsch.ProxySOCKS4(
                    proxy.getProxyHost(), proxy.getProxyPort());
                if (proxy.getProxyUsername() != null && !proxy.getProxyUsername().isEmpty()) {
                    socks4Proxy.setUserPasswd(proxy.getProxyUsername(), proxy.getProxyPassword());
                }
                session.setProxy(socks4Proxy);
                logger.info("Using SOCKS4 proxy: {}:{}", proxy.getProxyHost(), proxy.getProxyPort());
                break;
                
            case SOCKS5:
                com.jcraft.jsch.ProxySOCKS5 socks5Proxy = new com.jcraft.jsch.ProxySOCKS5(
                    proxy.getProxyHost(), proxy.getProxyPort());
                if (proxy.getProxyUsername() != null && !proxy.getProxyUsername().isEmpty()) {
                    socks5Proxy.setUserPasswd(proxy.getProxyUsername(), proxy.getProxyPassword());
                }
                session.setProxy(socks5Proxy);
                logger.info("Using SOCKS5 proxy: {}:{}", proxy.getProxyHost(), proxy.getProxyPort());
                break;
                
            case JUMP_HOST:
                // Jump host is handled separately in connectViaJumpHost()
                break;
                
            default:
                break;
        }
    }
    
    /**
     * Open shell channel for terminal
     */
    public void openShell() throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        try {
            shellChannel = (ChannelShell) session.openChannel("shell");
            
            // Terminal settings
            shellChannel.setPtyType(config.getTerminalType(), 
                config.getTerminalCols(), 
                config.getTerminalRows(), 
                0, 0);
            
            // Get streams
            inputStream = shellChannel.getInputStream();
            outputStream = shellChannel.getOutputStream();
            
            // Connect channel
            shellChannel.connect(config.getTimeout() > 0 ? config.getTimeout() : 30000);
            
            fireEvent(SSHEvent.SHELL_OPENED);
            logger.info("Shell channel opened");
            
        } catch (JSchException | IOException e) {
            lastError = e.getMessage();
            throw new SSHException("Failed to open shell: " + e.getMessage(), e);
        }
    }
    
    /**
     * Open SFTP channel
     */
    public ChannelSftp openSftp() throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        try {
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect(config.getTimeout() > 0 ? config.getTimeout() : 30000);
            
            fireEvent(SSHEvent.SFTP_OPENED);
            logger.info("SFTP channel opened");
            
            return sftpChannel;
        } catch (JSchException e) {
            lastError = e.getMessage();
            throw new SSHException("Failed to open SFTP: " + e.getMessage(), e);
        }
    }
    
    /**
     * Execute command and return output
     */
    public String exec(String command) throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        ChannelExec execChannel = null;
        try {
            execChannel = (ChannelExec) session.openChannel("exec");
            execChannel.setCommand(command);
            execChannel.setInputStream(null);
            
            InputStream in = execChannel.getInputStream();
            InputStream err = execChannel.getErrStream();
            
            execChannel.connect(config.getTimeout() > 0 ? config.getTimeout() : 30000);
            
            // Read output
            StringBuilder output = new StringBuilder();
            byte[] buffer = new byte[4096];
            
            while (true) {
                while (in.available() > 0) {
                    int len = in.read(buffer);
                    if (len < 0) break;
                    output.append(new String(buffer, 0, len, config.getCharset()));
                }
                
                while (err.available() > 0) {
                    int len = err.read(buffer);
                    if (len < 0) break;
                    output.append(new String(buffer, 0, len, config.getCharset()));
                }
                
                if (execChannel.isClosed()) {
                    if (in.available() > 0 || err.available() > 0) continue;
                    break;
                }
                
                Thread.sleep(100);
            }
            
            return output.toString();
            
        } catch (Exception e) {
            lastError = e.getMessage();
            throw new SSHException("Command execution failed: " + e.getMessage(), e);
        } finally {
            if (execChannel != null) {
                execChannel.disconnect();
            }
        }
    }
    
    /**
     * Setup port forwards from config
     */
    private void setupPortForwards() {
        if (config.getPortForwards() == null) return;
        
        for (PortForwardConfig pf : config.getPortForwards()) {
            if (!pf.isEnabled()) continue;
            
            try {
                switch (pf.getType()) {
                    case LOCAL:
                        addLocalPortForward(pf.getLocalPort(), pf.getRemoteHost(), pf.getRemotePort());
                        break;
                    case REMOTE:
                        addRemotePortForward(pf.getRemotePort(), pf.getLocalHost(), pf.getLocalPort());
                        break;
                    case DYNAMIC:
                        addDynamicPortForward(pf.getLocalPort());
                        break;
                }
            } catch (SSHException e) {
                logger.error("Port forward setup failed: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Add local port forward (L)
     */
    public void addLocalPortForward(int localPort, String remoteHost, int remotePort) throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        try {
            int assignedPort = session.setPortForwardingL(localPort, remoteHost, remotePort);
            String key = localPort + ":" + remoteHost + ":" + remotePort;
            localForwards.put(key, assignedPort);
            logger.info("Local forward: {} -> {}:{}", assignedPort, remoteHost, remotePort);
        } catch (JSchException e) {
            throw new SSHException("Local port forward failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Add remote port forward (R)
     */
    public void addRemotePortForward(int remotePort, String localHost, int localPort) throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        try {
            session.setPortForwardingR(remotePort, localHost, localPort);
            String key = remotePort + ":" + localHost + ":" + localPort;
            remoteForwards.put(key, remotePort);
            logger.info("Remote forward: {} -> {}:{}", remotePort, localHost, localPort);
        } catch (JSchException e) {
            throw new SSHException("Remote port forward failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Add dynamic port forward (D) - SOCKS proxy
     */
    public void addDynamicPortForward(int localPort) throws SSHException {
        if (!connected || session == null) {
            throw new SSHException("Not connected");
        }
        
        try {
            session.setPortForwardingL(localPort, "localhost", 0);
            logger.info("Dynamic forward (SOCKS): {}", localPort);
        } catch (JSchException e) {
            throw new SSHException("Dynamic port forward failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Resize terminal
     */
    public void resizeTerminal(int cols, int rows) {
        if (shellChannel != null && shellChannel.isConnected()) {
            shellChannel.setPtySize(cols, rows, cols * 8, rows * 16);
            logger.debug("Terminal resized: {}x{}", cols, rows);
        }
    }
    
    /**
     * Write to shell
     */
    public void write(byte[] data) throws IOException {
        if (outputStream != null) {
            outputStream.write(data);
            outputStream.flush();
        }
    }
    
    /**
     * Write string to shell
     */
    public void write(String text) throws IOException {
        write(text.getBytes(config.getCharset()));
    }
    
    /**
     * Get shell input stream
     */
    public InputStream getInputStream() {
        return inputStream;
    }
    
    /**
     * Get shell output stream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }
    
    /**
     * Disconnect session
     */
    public void disconnect() {
        if (shellChannel != null) {
            shellChannel.disconnect();
            shellChannel = null;
        }
        
        if (sftpChannel != null) {
            sftpChannel.disconnect();
            sftpChannel = null;
        }
        
        if (session != null) {
            session.disconnect();
            session = null;
        }
        
        connected = false;
        fireEvent(SSHEvent.DISCONNECTED);
        logger.info("Disconnected from {}:{}", config.getHost(), config.getPort());
    }
    
    // Listener management
    public void addListener(SSHSessionListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SSHSessionListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(SSHEvent event) {
        fireEvent(event, null);
    }
    
    private void fireEvent(SSHEvent event, String message) {
        for (SSHSessionListener listener : listeners) {
            try {
                listener.onSSHEvent(this, event, message);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    // Getters
    public boolean isConnected() {
        return connected && session != null && session.isConnected();
    }
    
    public boolean isShellOpen() {
        return shellChannel != null && shellChannel.isConnected();
    }
    
    public String getLastError() {
        return lastError;
    }
    
    public ConnectConfig getConfig() {
        return config;
    }
    
    public Session getJSchSession() {
        return session;
    }
    
    /**
     * SSH Events
     */
    public enum SSHEvent {
        CONNECTING,
        CONNECTED,
        SHELL_OPENED,
        SFTP_OPENED,
        DISCONNECTED,
        ERROR
    }
    
    /**
     * SSH Session Listener
     */
    public interface SSHSessionListener {
        void onSSHEvent(SSHSession session, SSHEvent event, String message);
    }
}
