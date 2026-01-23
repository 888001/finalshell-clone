package com.finalshell.parser;

/**
 * ss命令记录
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SSRecord {
    
    private String state;
    private String recvQ;
    private String sendQ;
    private String localAddress;
    private String peerAddress;
    private String process;
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getRecvQ() {
        return recvQ;
    }
    
    public void setRecvQ(String recvQ) {
        this.recvQ = recvQ;
    }
    
    public String getSendQ() {
        return sendQ;
    }
    
    public void setSendQ(String sendQ) {
        this.sendQ = sendQ;
    }
    
    public String getLocalAddress() {
        return localAddress;
    }
    
    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }
    
    public String getPeerAddress() {
        return peerAddress;
    }
    
    public void setPeerAddress(String peerAddress) {
        this.peerAddress = peerAddress;
    }
    
    public String getProcess() {
        return process;
    }
    
    public void setProcess(String process) {
        this.process = process;
    }
    
    public String getLocalPort() {
        if (localAddress == null) return "";
        int colonIndex = localAddress.lastIndexOf(':');
        if (colonIndex >= 0) {
            return localAddress.substring(colonIndex + 1);
        }
        return "";
    }
    
    public String getLocalHost() {
        if (localAddress == null) return "";
        int colonIndex = localAddress.lastIndexOf(':');
        if (colonIndex >= 0) {
            return localAddress.substring(0, colonIndex);
        }
        return localAddress;
    }
    
    @Override
    public String toString() {
        return String.format("%s %s -> %s", state, localAddress, peerAddress);
    }
}
