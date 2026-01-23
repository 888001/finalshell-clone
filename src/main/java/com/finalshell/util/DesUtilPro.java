package com.finalshell.util;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

/**
 * DES加密工具 (增强版)
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Crypto_Thread_DeepAnalysis.md - DesUtilPro
 */
public class DesUtilPro {
    
    private static final String ALGORITHM = "DES";
    private static final int HEAD_LENGTH = 1102;
    
    /**
     * 解密字符串
     */
    public static String decrypt(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            return null;
        }
        
        byte[] buf = Base64.getDecoder().decode(data);
        
        // 提取头部(密钥)
        byte[] head = new byte[HEAD_LENGTH];
        System.arraycopy(buf, 0, head, 0, head.length);
        
        // 提取加密数据
        byte[] d = new byte[buf.length - head.length];
        System.arraycopy(buf, head.length, d, 0, d.length);
        
        // 解密
        byte[] bt = decryptBytes(d, head);
        return new String(bt, "UTF-8");
    }
    
    /**
     * 加密字符串
     */
    public static String encrypt(String content) throws Exception {
        // 生成随机头部(密钥)
        byte[] head = generateRandomBytes(HEAD_LENGTH);
        
        // 加密数据
        byte[] d = encryptBytes(content.getBytes("UTF-8"), head);
        
        // 组合头部和密文
        byte[] result = new byte[head.length + d.length];
        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(d, 0, result, head.length, d.length);
        
        // Base64编码
        return Base64.getEncoder().encodeToString(result);
    }
    
    /**
     * 生成随机字节
     */
    private static byte[] generateRandomBytes(int len) {
        byte[] data = new byte[len];
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            data[i] = (byte) random.nextInt(127);
        }
        return data;
    }
    
    /**
     * 加密字节数组
     */
    public static byte[] encryptBytes(byte[] data) throws Exception {
        byte[] head = generateRandomBytes(HEAD_LENGTH);
        byte[] d = encryptBytes(data, head);
        
        byte[] result = new byte[head.length + d.length];
        System.arraycopy(head, 0, result, 0, head.length);
        System.arraycopy(d, 0, result, head.length, d.length);
        return result;
    }
    
    /**
     * 解密字节数组
     */
    public static byte[] decryptBytes(byte[] data) throws Exception {
        byte[] head = new byte[HEAD_LENGTH];
        System.arraycopy(data, 0, head, 0, head.length);
        
        byte[] d = new byte[data.length - head.length];
        System.arraycopy(data, head.length, d, 0, d.length);
        
        return decryptBytes(d, head);
    }
    
    /**
     * DES加密核心
     */
    public static byte[] encryptBytes(byte[] data, byte[] head) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(head);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
    
    /**
     * DES解密核心
     */
    public static byte[] decryptBytes(byte[] data, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(data);
    }
}
