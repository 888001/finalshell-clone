package com.finalshell.key;

import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

/**
 * SSH私钥实体类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SecretKey_Manager_DeepAnalysis.md
 */
public class SecretKey {
    
    private String id;
    private String name;
    private String password;
    private byte[] keyData;
    private boolean used;
    private int keyType;
    private long createTime;
    private long modifyTime;
    
    // 密钥类型常量
    public static final int TYPE_ERROR = 0;
    public static final int TYPE_DSA = 1;
    public static final int TYPE_RSA = 2;
    public static final int TYPE_ECDSA = 3;
    public static final int TYPE_ED25519 = 4;
    public static final int TYPE_UNKNOWN = 5;
    
    public SecretKey() {
        this.id = java.util.UUID.randomUUID().toString();
        this.createTime = System.currentTimeMillis();
        this.modifyTime = this.createTime;
        this.used = false;
    }
    
    public SecretKey(String name, byte[] keyData) {
        this();
        this.name = name;
        setKeyData(keyData);
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
        this.modifyTime = System.currentTimeMillis();
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public byte[] getKeyData() {
        return keyData;
    }
    
    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
        this.modifyTime = System.currentTimeMillis();
        
        // 自动检测密钥类型
        if (keyData != null) {
            try {
                KeyPair kpair = KeyPair.load(new JSch(), keyData, null);
                if (kpair != null) {
                    this.keyType = kpair.getKeyType();
                }
            } catch (JSchException e) {
                this.keyType = TYPE_UNKNOWN;
            }
        }
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public int getKeyType() {
        return keyType;
    }
    
    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getModifyTime() {
        return modifyTime;
    }
    
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    /**
     * 获取密钥类型字符串
     */
    public String getKeyTypeString() {
        return getKeyTypeString(this.keyType);
    }
    
    /**
     * 获取密钥类型字符串
     */
    public static String getKeyTypeString(int type) {
        switch (type) {
            case TYPE_DSA: return "DSA";
            case TYPE_RSA: return "RSA";
            case TYPE_ECDSA: return "ECDSA";
            case TYPE_ED25519: return "ED25519";
            case TYPE_ERROR: return "ERROR";
            default: return "UNKNOWN";
        }
    }
    
    /**
     * 获取公钥指纹
     */
    public String getFingerprint() {
        if (keyData == null) return "";
        try {
            KeyPair kpair = KeyPair.load(new JSch(), keyData, null);
            if (kpair != null) {
                return kpair.getFingerPrint();
            }
        } catch (JSchException e) {
            // ignore
        }
        return "";
    }
    
    @Override
    public String toString() {
        return name + " (" + getKeyTypeString() + ")";
    }
}
