package com.finalshell.ftp;

import java.io.Serializable;
import java.util.UUID;

/**
 * FTP Connection Configuration
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class FTPConfig implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public enum TransferMode { ACTIVE, PASSIVE }
    public enum Protocol { FTP, FTPS, FTPS_IMPLICIT }
    
    private String id;
    private String name;
    private String host;
    private int port = 21;
    private String username;
    private String password;
    
    // Connection settings
    private Protocol protocol = Protocol.FTP;
    private TransferMode transferMode = TransferMode.PASSIVE;
    private String charset = "UTF-8";
    private int connectionTimeout = 30000;
    private int dataTimeout = 30000;
    
    // SSL/TLS settings
    private boolean implicit = false;
    private boolean trustAllCerts = false;
    
    // Directory settings
    private String remoteDir = "/";
    private String localDir = System.getProperty("user.home");
    
    // Transfer settings
    private boolean binaryMode = true;
    private int bufferSize = 8192;
    
    public FTPConfig() {
        this.id = UUID.randomUUID().toString();
    }
    
    public FTPConfig(String name, String host, String username, String password) {
        this();
        this.name = name;
        this.host = host;
        this.username = username;
        this.password = password;
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
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Protocol getProtocol() { return protocol; }
    public void setProtocol(Protocol protocol) { this.protocol = protocol; }
    
    public TransferMode getTransferMode() { return transferMode; }
    public void setTransferMode(TransferMode transferMode) { this.transferMode = transferMode; }
    
    public String getCharset() { return charset; }
    public void setCharset(String charset) { this.charset = charset; }
    
    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }
    
    public int getDataTimeout() { return dataTimeout; }
    public void setDataTimeout(int dataTimeout) { this.dataTimeout = dataTimeout; }
    
    public boolean isImplicit() { return implicit; }
    public void setImplicit(boolean implicit) { this.implicit = implicit; }
    
    public boolean isTrustAllCerts() { return trustAllCerts; }
    public void setTrustAllCerts(boolean trustAllCerts) { this.trustAllCerts = trustAllCerts; }
    
    public String getRemoteDir() { return remoteDir; }
    public void setRemoteDir(String remoteDir) { this.remoteDir = remoteDir; }
    
    public String getLocalDir() { return localDir; }
    public void setLocalDir(String localDir) { this.localDir = localDir; }
    
    public boolean isBinaryMode() { return binaryMode; }
    public void setBinaryMode(boolean binaryMode) { this.binaryMode = binaryMode; }
    
    public int getBufferSize() { return bufferSize; }
    public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }
    
    @Override
    public String toString() {
        return name + " (" + host + ":" + port + ")";
    }
}
