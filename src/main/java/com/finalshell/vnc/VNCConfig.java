package com.finalshell.vnc;

import java.io.Serializable;
import java.util.UUID;

/**
 * VNC Connection Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class VNCConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private String host;
    private int port = 5900;
    private String password;
    
    // Display settings
    private int colorDepth = 24;  // 8, 16, 24, 32
    private boolean viewOnly = false;
    private boolean sharedConnection = true;
    private double scaleFactor = 1.0;
    
    // SSH tunnel settings
    private boolean useSshTunnel = false;
    private String sshHost;
    private int sshPort = 22;
    private String sshUser;
    private String sshPassword;
    private String sshKeyFile;
    
    // Encoding settings
    private String encoding = "Tight";  // Raw, RRE, Hextile, Tight, ZRLE
    private int compressionLevel = 6;   // 0-9
    private int jpegQuality = 6;        // 0-9
    
    public VNCConfig() {
        this.id = UUID.randomUUID().toString();
    }
    
    public VNCConfig(String name, String host, int port) {
        this();
        this.name = name;
        this.host = host;
        this.port = port;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getColorDepth() { return colorDepth; }
    public void setColorDepth(int colorDepth) { this.colorDepth = colorDepth; }
    
    public boolean isViewOnly() { return viewOnly; }
    public void setViewOnly(boolean viewOnly) { this.viewOnly = viewOnly; }
    
    public boolean isSharedConnection() { return sharedConnection; }
    public void setSharedConnection(boolean sharedConnection) { this.sharedConnection = sharedConnection; }
    
    public double getScaleFactor() { return scaleFactor; }
    public void setScaleFactor(double scaleFactor) { this.scaleFactor = scaleFactor; }
    
    public boolean isUseSshTunnel() { return useSshTunnel; }
    public void setUseSshTunnel(boolean useSshTunnel) { this.useSshTunnel = useSshTunnel; }
    
    public String getSshHost() { return sshHost; }
    public void setSshHost(String sshHost) { this.sshHost = sshHost; }
    
    public int getSshPort() { return sshPort; }
    public void setSshPort(int sshPort) { this.sshPort = sshPort; }
    
    public String getSshUser() { return sshUser; }
    public void setSshUser(String sshUser) { this.sshUser = sshUser; }
    
    public String getSshPassword() { return sshPassword; }
    public void setSshPassword(String sshPassword) { this.sshPassword = sshPassword; }
    
    public String getSshKeyFile() { return sshKeyFile; }
    public void setSshKeyFile(String sshKeyFile) { this.sshKeyFile = sshKeyFile; }
    
    public String getEncoding() { return encoding; }
    public void setEncoding(String encoding) { this.encoding = encoding; }
    
    public int getCompressionLevel() { return compressionLevel; }
    public void setCompressionLevel(int compressionLevel) { this.compressionLevel = compressionLevel; }
    
    public int getJpegQuality() { return jpegQuality; }
    public void setJpegQuality(int jpegQuality) { this.jpegQuality = jpegQuality; }
    
    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
