package com.finalshell.rdp;

/**
 * RDP Configuration - Remote Desktop Protocol settings
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Network_Protocol_Analysis.md - RDP Protocol
 */
public class RDPConfig {
    
    private String id;
    private String name;
    
    // RDP target
    private String rdpHost = "localhost";
    private int rdpPort = 3389;
    private String rdpUsername;
    private String rdpPassword;
    private String rdpDomain;
    
    // Display settings
    private int width = 1920;
    private int height = 1080;
    private int colorDepth = 32;
    private boolean fullscreen = false;
    
    // SSH tunnel settings (for RDP over SSH)
    private boolean useSshTunnel = true;
    private String sshConnectionId;
    private int localTunnelPort = 33890;
    
    // Advanced options
    private boolean enableNLA = true;
    private boolean enableSound = false;
    private boolean enableClipboard = true;
    private boolean enableDrives = false;
    private String drivesToRedirect;
    
    public RDPConfig() {
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRdpHost() { return rdpHost; }
    public void setRdpHost(String rdpHost) { this.rdpHost = rdpHost; }
    
    public int getRdpPort() { return rdpPort; }
    public void setRdpPort(int rdpPort) { this.rdpPort = rdpPort; }
    
    public String getRdpUsername() { return rdpUsername; }
    public void setRdpUsername(String rdpUsername) { this.rdpUsername = rdpUsername; }
    
    public String getRdpPassword() { return rdpPassword; }
    public void setRdpPassword(String rdpPassword) { this.rdpPassword = rdpPassword; }
    
    public String getRdpDomain() { return rdpDomain; }
    public void setRdpDomain(String rdpDomain) { this.rdpDomain = rdpDomain; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public int getColorDepth() { return colorDepth; }
    public void setColorDepth(int colorDepth) { this.colorDepth = colorDepth; }
    
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean fullscreen) { this.fullscreen = fullscreen; }
    
    public boolean isUseSshTunnel() { return useSshTunnel; }
    public void setUseSshTunnel(boolean useSshTunnel) { this.useSshTunnel = useSshTunnel; }
    
    public String getSshConnectionId() { return sshConnectionId; }
    public void setSshConnectionId(String sshConnectionId) { this.sshConnectionId = sshConnectionId; }
    
    public int getLocalTunnelPort() { return localTunnelPort; }
    public void setLocalTunnelPort(int localTunnelPort) { this.localTunnelPort = localTunnelPort; }
    
    public boolean isEnableNLA() { return enableNLA; }
    public void setEnableNLA(boolean enableNLA) { this.enableNLA = enableNLA; }
    
    public boolean isEnableSound() { return enableSound; }
    public void setEnableSound(boolean enableSound) { this.enableSound = enableSound; }
    
    public boolean isEnableClipboard() { return enableClipboard; }
    public void setEnableClipboard(boolean enableClipboard) { this.enableClipboard = enableClipboard; }
    
    public boolean isEnableDrives() { return enableDrives; }
    public void setEnableDrives(boolean enableDrives) { this.enableDrives = enableDrives; }
    
    public String getDrivesToRedirect() { return drivesToRedirect; }
    public void setDrivesToRedirect(String drivesToRedirect) { this.drivesToRedirect = drivesToRedirect; }
    
    /**
     * Get resolution string
     */
    public String getResolutionString() {
        return width + "x" + height;
    }
    
    /**
     * Get effective RDP host (localhost if tunneled)
     */
    public String getEffectiveHost() {
        return useSshTunnel ? "localhost" : rdpHost;
    }
    
    /**
     * Get effective RDP port (tunnel port if tunneled)
     */
    public int getEffectivePort() {
        return useSshTunnel ? localTunnelPort : rdpPort;
    }
}
