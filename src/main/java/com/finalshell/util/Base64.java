package com.finalshell.util;

/**
 * Base64编解码器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: COMPLETE_SUMMARY.md - Base64
 */
public class Base64 {
    
    private static final char[] ALPHABET = 
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    
    private static final int[] DECODE_TABLE = new int[128];
    
    static {
        for (int i = 0; i < DECODE_TABLE.length; i++) {
            DECODE_TABLE[i] = -1;
        }
        for (int i = 0; i < ALPHABET.length; i++) {
            DECODE_TABLE[ALPHABET[i]] = i;
        }
        DECODE_TABLE['='] = 0;
    }
    
    private Base64() {}
    
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        int remainder = data.length % 3;
        int mainLength = data.length - remainder;
        
        for (int i = 0; i < mainLength; i += 3) {
            int a = (data[i] & 0xff) << 16 | (data[i + 1] & 0xff) << 8 | (data[i + 2] & 0xff);
            sb.append(ALPHABET[(a >> 18) & 0x3f]);
            sb.append(ALPHABET[(a >> 12) & 0x3f]);
            sb.append(ALPHABET[(a >> 6) & 0x3f]);
            sb.append(ALPHABET[a & 0x3f]);
        }
        
        if (remainder == 1) {
            int a = (data[mainLength] & 0xff) << 16;
            sb.append(ALPHABET[(a >> 18) & 0x3f]);
            sb.append(ALPHABET[(a >> 12) & 0x3f]);
            sb.append("==");
        } else if (remainder == 2) {
            int a = (data[mainLength] & 0xff) << 16 | (data[mainLength + 1] & 0xff) << 8;
            sb.append(ALPHABET[(a >> 18) & 0x3f]);
            sb.append(ALPHABET[(a >> 12) & 0x3f]);
            sb.append(ALPHABET[(a >> 6) & 0x3f]);
            sb.append('=');
        }
        
        return sb.toString();
    }
    
    public static String encode(String data) {
        if (data == null) {
            return "";
        }
        return encode(data.getBytes());
    }
    
    public static byte[] decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return new byte[0];
        }
        
        encoded = encoded.replaceAll("\\s", "");
        
        int padding = 0;
        if (encoded.endsWith("==")) {
            padding = 2;
        } else if (encoded.endsWith("=")) {
            padding = 1;
        }
        
        int length = (encoded.length() * 3) / 4 - padding;
        byte[] result = new byte[length];
        
        int index = 0;
        for (int i = 0; i < encoded.length(); i += 4) {
            int a = DECODE_TABLE[encoded.charAt(i)];
            int b = DECODE_TABLE[encoded.charAt(i + 1)];
            int c = DECODE_TABLE[encoded.charAt(i + 2)];
            int d = DECODE_TABLE[encoded.charAt(i + 3)];
            
            int value = (a << 18) | (b << 12) | (c << 6) | d;
            
            if (index < length) result[index++] = (byte) ((value >> 16) & 0xff);
            if (index < length) result[index++] = (byte) ((value >> 8) & 0xff);
            if (index < length) result[index++] = (byte) (value & 0xff);
        }
        
        return result;
    }
    
    public static String decodeToString(String encoded) {
        return new String(decode(encoded));
    }
}
