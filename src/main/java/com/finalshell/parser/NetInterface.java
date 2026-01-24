package com.finalshell.parser;

/**
 * Network Interface - Represents a network interface
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetInterface {
    
    private String name;
    private String ipv4;
    private String ipv6;
    private String mac;
    private String status;
    private long rxBytes;
    private long txBytes;
    
    public NetInterface() {}
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIpv4() { return ipv4; }
    public void setIpv4(String ipv4) { this.ipv4 = ipv4; }
    
    public String getIpv6() { return ipv6; }
    public void setIpv6(String ipv6) { this.ipv6 = ipv6; }
    
    public String getMac() { return mac; }
    public void setMac(String mac) { this.mac = mac; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public long getRxBytes() { return rxBytes; }
    public void setRxBytes(long rxBytes) { this.rxBytes = rxBytes; }
    
    public long getTxBytes() { return txBytes; }
    public void setTxBytes(long txBytes) { this.txBytes = txBytes; }
    
    @Override
    public String toString() {
        return name + " (" + (ipv4 != null ? ipv4 : "no ip") + ")";
    }
}
