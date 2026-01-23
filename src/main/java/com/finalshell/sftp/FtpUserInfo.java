package com.finalshell.sftp;

/**
 * FTP用户信息
 * 存储FTP连接的用户认证信息
 */
public class FtpUserInfo {
    
    private String host;
    private int port;
    private String username;
    private String password;
    private String privateKey;
    private String passphrase;
    private String protocol;
    private boolean savePassword;
    
    public FtpUserInfo() {
        this.port = 22;
        this.protocol = "sftp";
        this.savePassword = true;
    }
    
    public FtpUserInfo(String host, String username, String password) {
        this();
        this.host = host;
        this.username = username;
        this.password = password;
    }
    
    public FtpUserInfo(String host, int port, String username, String password) {
        this(host, username, password);
        this.port = port;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
    
    public String getPassphrase() {
        return passphrase;
    }
    
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
    public boolean isSavePassword() {
        return savePassword;
    }
    
    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }
    
    public boolean hasPrivateKey() {
        return privateKey != null && !privateKey.isEmpty();
    }
    
    public String getConnectionString() {
        return protocol + "://" + username + "@" + host + ":" + port;
    }
    
    @Override
    public String toString() {
        return getConnectionString();
    }
}
