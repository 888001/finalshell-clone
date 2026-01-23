package com.finalshell.util;

/**
 * 字节与字节转换工具
 */
public class ByteByteSwitch {
    
    public static byte toByte(short sh1) {
        return (byte)(sh1 & 0xFF);
    }
    
    public static short toShort(byte b) {
        return (short)(b & 0xFF);
    }
}
