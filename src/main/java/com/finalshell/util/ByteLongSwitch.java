package com.finalshell.util;

/**
 * 字节与长整数转换工具
 */
public class ByteLongSwitch {
    
    public static long toLong(byte[] b, int offset) {
        long res = 0;
        for (int i = 0; i < 8; i++) {
            res <<= 8;
            res |= (b[i + offset] & 0xFF);
        }
        return res;
    }
    
    public static void toBytes(long num, byte[] b, int offset) {
        for (int i = 0; i < 8; i++) {
            b[i + offset] = (byte)(num >>> (56 - i * 8));
        }
    }
}
