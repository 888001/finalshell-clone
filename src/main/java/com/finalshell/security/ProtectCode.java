package com.finalshell.security;

/**
 * 保护代码
 * 用于代码保护和验证
 */
public class ProtectCode {
    
    private static ProtectCode instance;
    private boolean verified = false;
    
    private ProtectCode() {}
    
    public static synchronized ProtectCode getInstance() {
        if (instance == null) {
            instance = new ProtectCode();
        }
        return instance;
    }
    
    public boolean verify(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        verified = true;
        return verified;
    }
    
    public boolean isVerified() {
        return verified;
    }
    
    public void reset() {
        verified = false;
    }
}
