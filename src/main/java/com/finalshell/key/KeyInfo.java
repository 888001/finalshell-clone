package com.finalshell.key;

/**
 * 密钥信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class KeyInfo {
    
    private String id;
    private String name;
    private String keyFile;
    private String publicKeyFile;
    private String passphrase;
    private String type;
    private String comment;
    private String fingerprint;
    private int bits;
    private long createTime;
    
    public KeyInfo() {
        this.id = java.util.UUID.randomUUID().toString();
        this.type = "RSA";
        this.bits = 2048;
        this.createTime = System.currentTimeMillis();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getKeyFile() {
        return keyFile;
    }
    
    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }
    
    public String getPublicKeyFile() {
        return publicKeyFile;
    }
    
    public void setPublicKeyFile(String publicKeyFile) {
        this.publicKeyFile = publicKeyFile;
    }
    
    public String getPassphrase() {
        return passphrase;
    }
    
    public void setPassphrase(String passphrase) {
        this.passphrase = passphrase;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getFingerprint() {
        return fingerprint;
    }
    
    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
    
    public int getBits() {
        return bits;
    }
    
    public void setBits(int bits) {
        this.bits = bits;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public String getPrivateKeyPath() { return keyFile; }
    public void setPrivateKeyPath(String path) { this.keyFile = path; }
    
    public String getPublicKeyPath() { return publicKeyFile; }
    public void setPublicKeyPath(String path) { this.publicKeyFile = path; }
    
    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
