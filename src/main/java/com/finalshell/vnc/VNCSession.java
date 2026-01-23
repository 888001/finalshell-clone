package com.finalshell.vnc;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * VNC Session - Manages VNC connection with optional SSH tunnel
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class VNCSession {
    
    private static final Logger logger = LoggerFactory.getLogger(VNCSession.class);
    
    private final VNCConfig config;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    
    // SSH tunnel
    private Session sshSession;
    private int localPort;
    
    // VNC protocol
    private int serverMajor, serverMinor;
    private int frameWidth, frameHeight;
    private int bitsPerPixel, depth;
    private String serverName;
    
    private boolean connected = false;
    private final List<VNCListener> listeners = new ArrayList<>();
    
    public VNCSession(VNCConfig config) {
        this.config = config;
    }
    
    /**
     * Connect to VNC server
     */
    public void connect() throws VNCException {
        try {
            String targetHost = config.getHost();
            int targetPort = config.getPort();
            
            // Setup SSH tunnel if needed
            if (config.isUseSshTunnel()) {
                setupSshTunnel();
                targetHost = "127.0.0.1";
                targetPort = localPort;
            }
            
            // Connect to VNC server
            logger.info("Connecting to VNC server {}:{}", targetHost, targetPort);
            socket = new Socket(targetHost, targetPort);
            socket.setTcpNoDelay(true);
            
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            // VNC handshake
            performHandshake();
            
            connected = true;
            fireEvent(VNCEvent.CONNECTED, "Connected to " + config.getHost());
            
        } catch (Exception e) {
            throw new VNCException("Failed to connect: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup SSH tunnel
     */
    private void setupSshTunnel() throws VNCException {
        try {
            JSch jsch = new JSch();
            
            // Add key file if specified
            if (config.getSshKeyFile() != null && !config.getSshKeyFile().isEmpty()) {
                jsch.addIdentity(config.getSshKeyFile());
            }
            
            sshSession = jsch.getSession(config.getSshUser(), config.getSshHost(), config.getSshPort());
            
            if (config.getSshPassword() != null && !config.getSshPassword().isEmpty()) {
                sshSession.setPassword(config.getSshPassword());
            }
            
            sshSession.setConfig("StrictHostKeyChecking", "no");
            sshSession.connect(30000);
            
            // Create local port forward
            localPort = sshSession.setPortForwardingL(0, config.getHost(), config.getPort());
            logger.info("SSH tunnel established: localhost:{} -> {}:{}", localPort, config.getHost(), config.getPort());
            
        } catch (JSchException e) {
            throw new VNCException("Failed to setup SSH tunnel: " + e.getMessage(), e);
        }
    }
    
    /**
     * Perform VNC protocol handshake
     */
    private void performHandshake() throws VNCException {
        try {
            // Read server version
            byte[] version = new byte[12];
            in.readFully(version);
            String serverVersion = new String(version);
            logger.debug("Server version: {}", serverVersion.trim());
            
            // Parse version
            if (serverVersion.startsWith("RFB ")) {
                serverMajor = Integer.parseInt(serverVersion.substring(4, 7));
                serverMinor = Integer.parseInt(serverVersion.substring(8, 11));
            }
            
            // Send client version (use 3.8)
            out.write("RFB 003.008\n".getBytes());
            out.flush();
            
            // Security negotiation
            int numSecTypes = in.readUnsignedByte();
            if (numSecTypes == 0) {
                int reasonLen = in.readInt();
                byte[] reason = new byte[reasonLen];
                in.readFully(reason);
                throw new VNCException("Connection failed: " + new String(reason));
            }
            
            byte[] secTypes = new byte[numSecTypes];
            in.readFully(secTypes);
            
            // Choose security type (prefer VNC auth, then none)
            int chosenType = 0;
            for (byte type : secTypes) {
                if (type == 2) { // VNC auth
                    chosenType = 2;
                    break;
                } else if (type == 1) { // None
                    chosenType = 1;
                }
            }
            
            out.writeByte(chosenType);
            out.flush();
            
            // Handle authentication
            if (chosenType == 2) {
                performVncAuth();
            }
            
            // Check auth result
            int authResult = in.readInt();
            if (authResult != 0) {
                throw new VNCException("Authentication failed");
            }
            
            // Client init - request shared
            out.writeByte(config.isSharedConnection() ? 1 : 0);
            out.flush();
            
            // Server init
            frameWidth = in.readUnsignedShort();
            frameHeight = in.readUnsignedShort();
            
            // Pixel format
            bitsPerPixel = in.readUnsignedByte();
            depth = in.readUnsignedByte();
            in.skipBytes(2); // big-endian, true-color
            in.skipBytes(6); // RGB max and shift
            in.skipBytes(3); // padding
            
            // Server name
            int nameLen = in.readInt();
            byte[] nameBytes = new byte[nameLen];
            in.readFully(nameBytes);
            serverName = new String(nameBytes);
            
            logger.info("VNC server: {} ({}x{}, {} bpp)", serverName, frameWidth, frameHeight, bitsPerPixel);
            
        } catch (IOException e) {
            throw new VNCException("Handshake failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Perform VNC authentication
     */
    private void performVncAuth() throws VNCException, IOException {
        // Read challenge
        byte[] challenge = new byte[16];
        in.readFully(challenge);
        
        // Encrypt with DES
        String password = config.getPassword();
        if (password == null || password.isEmpty()) {
            throw new VNCException("VNC password required");
        }
        
        byte[] key = new byte[8];
        byte[] pwBytes = password.getBytes();
        System.arraycopy(pwBytes, 0, key, 0, Math.min(pwBytes.length, 8));
        
        // Reverse bits in key (VNC specific)
        for (int i = 0; i < 8; i++) {
            key[i] = reverseBits(key[i]);
        }
        
        // DES encrypt
        byte[] response = desEncrypt(challenge, key);
        
        out.write(response);
        out.flush();
    }
    
    private byte reverseBits(byte b) {
        int result = 0;
        for (int i = 0; i < 8; i++) {
            result |= ((b >> i) & 1) << (7 - i);
        }
        return (byte) result;
    }
    
    private byte[] desEncrypt(byte[] data, byte[] key) {
        try {
            javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DES/ECB/NoPadding");
            javax.crypto.spec.SecretKeySpec keySpec = new javax.crypto.spec.SecretKeySpec(key, "DES");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("DES encryption failed", e);
            return new byte[16];
        }
    }
    
    /**
     * Disconnect from VNC server
     */
    public void disconnect() {
        connected = false;
        
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.debug("Error closing socket", e);
        }
        
        if (sshSession != null && sshSession.isConnected()) {
            sshSession.disconnect();
        }
        
        fireEvent(VNCEvent.DISCONNECTED, "Disconnected");
        logger.info("VNC session disconnected");
    }
    
    /**
     * Send key event
     */
    public void sendKeyEvent(int key, boolean down) throws IOException {
        if (!connected) return;
        
        out.writeByte(4); // Key event
        out.writeByte(down ? 1 : 0);
        out.writeShort(0); // padding
        out.writeInt(key);
        out.flush();
    }
    
    /**
     * Send pointer event
     */
    public void sendPointerEvent(int x, int y, int buttonMask) throws IOException {
        if (!connected) return;
        
        out.writeByte(5); // Pointer event
        out.writeByte(buttonMask);
        out.writeShort(x);
        out.writeShort(y);
        out.flush();
    }
    
    /**
     * Request framebuffer update
     */
    public void requestFramebufferUpdate(boolean incremental) throws IOException {
        requestFramebufferUpdate(incremental, 0, 0, frameWidth, frameHeight);
    }
    
    public void requestFramebufferUpdate(boolean incremental, int x, int y, int w, int h) throws IOException {
        if (!connected) return;
        
        out.writeByte(3); // Framebuffer update request
        out.writeByte(incremental ? 1 : 0);
        out.writeShort(x);
        out.writeShort(y);
        out.writeShort(w);
        out.writeShort(h);
        out.flush();
    }
    
    // Listener management
    public void addListener(VNCListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(VNCListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(VNCEvent event, String message) {
        for (VNCListener listener : listeners) {
            listener.onVNCEvent(this, event, message);
        }
    }
    
    // Getters
    public boolean isConnected() { return connected; }
    public int getFrameWidth() { return frameWidth; }
    public int getFrameHeight() { return frameHeight; }
    public String getServerName() { return serverName; }
    public DataInputStream getInputStream() { return in; }
    public DataOutputStream getOutputStream() { return out; }
    
    public enum VNCEvent {
        CONNECTING, CONNECTED, DISCONNECTED, ERROR
    }
    
    public interface VNCListener {
        void onVNCEvent(VNCSession session, VNCEvent event, String message);
    }
}
