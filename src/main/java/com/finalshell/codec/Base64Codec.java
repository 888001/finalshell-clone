package com.finalshell.codec;

/**
 * Base64编解码器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - Base64
 */
public class Base64Codec {
    
    private static final char[] ENCODE_TABLE = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };
    
    private static final int[] DECODE_TABLE = new int[128];
    
    static {
        for (int i = 0; i < DECODE_TABLE.length; i++) {
            DECODE_TABLE[i] = -1;
        }
        for (int i = 0; i < ENCODE_TABLE.length; i++) {
            DECODE_TABLE[ENCODE_TABLE[i]] = i;
        }
        DECODE_TABLE['='] = 0;
    }
    
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        int remainder = data.length % 3;
        int mainPart = data.length - remainder;
        
        for (int i = 0; i < mainPart; i += 3) {
            int b0 = data[i] & 0xFF;
            int b1 = data[i + 1] & 0xFF;
            int b2 = data[i + 2] & 0xFF;
            
            sb.append(ENCODE_TABLE[b0 >> 2]);
            sb.append(ENCODE_TABLE[((b0 & 0x03) << 4) | (b1 >> 4)]);
            sb.append(ENCODE_TABLE[((b1 & 0x0F) << 2) | (b2 >> 6)]);
            sb.append(ENCODE_TABLE[b2 & 0x3F]);
        }
        
        if (remainder == 1) {
            int b0 = data[mainPart] & 0xFF;
            sb.append(ENCODE_TABLE[b0 >> 2]);
            sb.append(ENCODE_TABLE[(b0 & 0x03) << 4]);
            sb.append("==");
        } else if (remainder == 2) {
            int b0 = data[mainPart] & 0xFF;
            int b1 = data[mainPart + 1] & 0xFF;
            sb.append(ENCODE_TABLE[b0 >> 2]);
            sb.append(ENCODE_TABLE[((b0 & 0x03) << 4) | (b1 >> 4)]);
            sb.append(ENCODE_TABLE[(b1 & 0x0F) << 2]);
            sb.append('=');
        }
        
        return sb.toString();
    }
    
    public static byte[] decode(String str) {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }
        
        // 移除空白字符
        str = str.replaceAll("\\s", "");
        
        int padding = 0;
        if (str.endsWith("==")) {
            padding = 2;
        } else if (str.endsWith("=")) {
            padding = 1;
        }
        
        int outputLen = (str.length() * 3) / 4 - padding;
        byte[] result = new byte[outputLen];
        
        int j = 0;
        for (int i = 0; i < str.length(); i += 4) {
            int c0 = DECODE_TABLE[str.charAt(i)];
            int c1 = DECODE_TABLE[str.charAt(i + 1)];
            int c2 = DECODE_TABLE[str.charAt(i + 2)];
            int c3 = DECODE_TABLE[str.charAt(i + 3)];
            
            if (j < outputLen) result[j++] = (byte) ((c0 << 2) | (c1 >> 4));
            if (j < outputLen) result[j++] = (byte) ((c1 << 4) | (c2 >> 2));
            if (j < outputLen) result[j++] = (byte) ((c2 << 6) | c3);
        }
        
        return result;
    }
    
    public static String encodeString(String str) {
        try {
            return encode(str.getBytes("UTF-8"));
        } catch (Exception e) {
            return encode(str.getBytes());
        }
    }
    
    public static String decodeString(String str) {
        try {
            return new String(decode(str), "UTF-8");
        } catch (Exception e) {
            return new String(decode(str));
        }
    }
}
