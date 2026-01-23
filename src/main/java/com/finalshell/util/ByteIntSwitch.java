package com.finalshell.util;

/**
 * 字节与整数转换工具
 */
public class ByteIntSwitch {
    
    public static int toInt(byte[] b, int offset) {
        return b[offset + 3] & 0xFF | 
               (b[offset + 2] & 0xFF) << 8 | 
               (b[offset + 1] & 0xFF) << 16 | 
               (b[offset] & 0xFF) << 24;
    }
    
    public static void toBytes(int n, byte[] buf, int offset) {
        buf[offset] = (byte)(n >> 24);
        buf[offset + 1] = (byte)(n >> 16);
        buf[offset + 2] = (byte)(n >> 8);
        buf[offset + 3] = (byte)n;
    }
}
