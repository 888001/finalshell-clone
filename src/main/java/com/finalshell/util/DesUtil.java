package com.finalshell.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

/**
 * DES加密工具类 - 兼容原版密码加密
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DesUtil_Analysis.md, Encryption_Tools_DeepAnalysis.md
 * 
 * 注意：DES加密已过时，仅用于兼容原版配置文件
 */
public class DesUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DesUtil.class);
    
    private static final int DES_SEED = 324534;
    private static final String DES_ALGORITHM = "DES";
    private static final String DES_TRANSFORMATION = "DES/ECB/PKCS5Padding";
    
    private static final String AES_KEY = "cwX5*ZKc$xCpz6dS";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    private static byte[] desKey;
    private static SecretKey aesSecretKey;
    
    static {
        try {
            // 初始化DES密钥
            SecureRandom random = new SecureRandom();
            random.setSeed(DES_SEED);
            KeyGenerator keyGen = KeyGenerator.getInstance(DES_ALGORITHM);
            keyGen.init(56, random);
            desKey = keyGen.generateKey().getEncoded();
            
            // 初始化AES密钥
            aesSecretKey = new SecretKeySpec(AES_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            
        } catch (Exception e) {
            logger.error("Failed to initialize encryption keys", e);
        }
    }
    
    /**
     * DES加密 (兼容原版)
     */
    public static String encryptDES(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            SecretKeySpec keySpec = new SecretKeySpec(desKey, DES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            logger.error("DES encryption failed", e);
            return null;
        }
    }
    
    /**
     * DES解密 (兼容原版)
     */
    public static String decryptDES(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            SecretKeySpec keySpec = new SecretKeySpec(desKey, DES_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("DES decryption failed", e);
            return null;
        }
    }
    
    /**
     * AES加密 (推荐使用)
     */
    public static String encryptAES(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, aesSecretKey);
            
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
            
        } catch (Exception e) {
            logger.error("AES encryption failed", e);
            return null;
        }
    }
    
    /**
     * AES解密
     */
    public static String decryptAES(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, aesSecretKey);
            
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("AES decryption failed", e);
            return null;
        }
    }
    
    /**
     * 默认加密 (使用AES)
     */
    public static String encrypt(String plainText) {
        return encryptAES(plainText);
    }
    
    /**
     * 默认解密 (自动检测DES或AES)
     */
    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        // 先尝试AES解密
        String result = decryptAES(encryptedText);
        if (result != null) {
            return result;
        }
        
        // 回退到DES解密 (兼容旧配置)
        return decryptDES(encryptedText);
    }
    
    /**
     * 计算MD5哈希
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (Exception e) {
            logger.error("MD5 hash failed", e);
            return null;
        }
    }
    
    /**
     * 计算SHA256哈希
     */
    public static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (Exception e) {
            logger.error("SHA256 hash failed", e);
            return null;
        }
    }
}
