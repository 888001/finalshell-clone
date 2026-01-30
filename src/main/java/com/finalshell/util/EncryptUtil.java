package com.finalshell.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Encryption Utility - DES and AES encryption/decryption
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Implementation_Guide.md - Encryption Algorithms
 * 
 * Note: DES is used for local password storage (compatibility with original FinalShell)
 *       AES is used for sync data (more secure)
 */
public class EncryptUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(EncryptUtil.class);
    
    // DES key for local storage (same as original FinalShell for compatibility)
    private static final String DES_KEY = "finalshell";
    
    public static final String PASSWORD_PREFIX = "enc:";
    
    // AES settings for sync
    private static final String AES_SALT = "finalshell_sync";
    private static final int AES_ITERATIONS = 65536;
    private static final int AES_KEY_LENGTH = 256;
    
    /**
     * Encrypt string using DES (for local password storage)
     */
    public static String encryptDES(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        if (plainText.startsWith(PASSWORD_PREFIX)) {
            return plainText;
        }

        if (isLegacyDESEncrypted(plainText)) {
            return PASSWORD_PREFIX + plainText;
        }

        String raw = encryptDESRaw(plainText);
        if (raw == null) {
            return plainText;
        }
        return PASSWORD_PREFIX + raw;
    }
    
    /**
     * Decrypt string using DES (for local password storage)
     */
    public static String decryptDES(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        if (encryptedText.startsWith(PASSWORD_PREFIX)) {
            String raw = encryptedText.substring(PASSWORD_PREFIX.length());
            String plain = decryptDESInternal(raw, true);
            return plain != null ? plain : raw;
        }

        if (isLegacyDESEncrypted(encryptedText)) {
            String plain = decryptDESInternal(encryptedText, true);
            return plain != null ? plain : encryptedText;
        }

        return encryptedText;
    }
    
    /**
     * Encrypt string using AES-256 (for sync data)
     */
    public static String encryptAES(String plainText, String password) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            byte[] salt = AES_SALT.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, AES_ITERATIONS, AES_KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            System.arraycopy(salt, 0, iv, 0, Math.min(salt.length, 16));
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error("AES encryption failed", e);
            return plainText;
        }
    }
    
    /**
     * Decrypt string using AES-256 (for sync data)
     */
    public static String decryptAES(String encryptedText, String password) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        try {
            byte[] salt = AES_SALT.getBytes(StandardCharsets.UTF_8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, AES_ITERATIONS, AES_KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
            
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] iv = new byte[16];
            System.arraycopy(salt, 0, iv, 0, Math.min(salt.length, 16));
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("AES decryption failed", e);
            return encryptedText;
        }
    }
    
    /**
     * Check if a string appears to be encrypted (Base64 encoded)
     */
    public static boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.startsWith(PASSWORD_PREFIX);
    }

    public static boolean isDESEncrypted(String text) {
        return isEncrypted(text) || isLegacyDESEncrypted(text);
    }

    private static boolean isLegacyDESEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(text);
            if (decoded.length == 0 || (decoded.length % 8) != 0) {
                return false;
            }
            String plain = decryptDESInternal(text, false);
            if (plain == null || plain.isEmpty() || plain.equals(text)) {
                return false;
            }

            if (!isLikelyHumanText(plain)) {
                return false;
            }

            String re = encryptDESRaw(plain);
            return re != null && re.equals(text);
        } catch (Exception e) {
            return false;
        }
    }

    private static String encryptDESRaw(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            logger.error("DES encryption failed", e);
            return null;
        }
    }

    private static String decryptDESInternal(String rawBase64, boolean logError) {
        if (rawBase64 == null || rawBase64.isEmpty()) {
            return null;
        }
        try {
            DESKeySpec keySpec = new DESKeySpec(DES_KEY.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(rawBase64));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            if (logError) {
                logger.error("DES decryption failed", e);
            }
            return null;
        }
    }

    private static boolean isLikelyHumanText(String s) {
        int len = s.length();
        if (len == 0) {
            return false;
        }
        int printable = 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\n' || c == '\r' || c == '\t') {
                printable++;
                continue;
            }
            if (c >= 32 && c != 127) {
                printable++;
            }
        }
        return printable * 1.0 / len >= 0.85;
    }
}
