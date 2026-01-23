package com.finalshell.util;

/**
 * 字节与短整数转换工具
 */
public final class ByteShortSwitch {
    
    public static byte[] toBytes(short i, byte[] b, int offset) {
        b[offset] = (byte)(i >> 8);
        b[offset + 1] = (byte)(i);
        return b;
    }
    
    public static short toShort(byte[] b, int offset) {
        return (short)(b[offset] << 8 | b[offset + 1] & 0xFF);
    }
    
    public static byte[] toBytes(int s, byte[] b, int offset) {
        b[offset] = (byte)(s >> 8);
        b[offset + 1] = (byte)(s);
        return b;
    }
    
    public static int toUnsignedShort(byte[] b, int offset) {
        int i = 0;
        i |= b[offset] & 0xFF;
        i <<= 8;
        i |= b[offset + 1] & 0xFF;
        return i;
    }
}
