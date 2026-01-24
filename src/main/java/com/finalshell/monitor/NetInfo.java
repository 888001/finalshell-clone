package com.finalshell.monitor;

/**
 * Network Info - Represents network interface information
 */
public class NetInfo {
    
    private String name;
    private long rxBytes;
    private long txBytes;
    private long rxPackets;
    private long txPackets;
    private long rxErrors;
    private long txErrors;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getRxBytes() { return rxBytes; }
    public void setRxBytes(long rxBytes) { this.rxBytes = rxBytes; }
    
    public long getTxBytes() { return txBytes; }
    public void setTxBytes(long txBytes) { this.txBytes = txBytes; }
    
    public long getRxPackets() { return rxPackets; }
    public void setRxPackets(long rxPackets) { this.rxPackets = rxPackets; }
    
    public long getTxPackets() { return txPackets; }
    public void setTxPackets(long txPackets) { this.txPackets = txPackets; }
    
    public long getRxErrors() { return rxErrors; }
    public void setRxErrors(long rxErrors) { this.rxErrors = rxErrors; }
    
    public long getTxErrors() { return txErrors; }
    public void setTxErrors(long txErrors) { this.txErrors = txErrors; }
    
    public void setInterfaceName(String name) { this.name = name; }
}
