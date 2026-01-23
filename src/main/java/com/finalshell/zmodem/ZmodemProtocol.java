package com.finalshell.zmodem;

/**
 * Zmodem协议常量
 */
public class ZmodemProtocol {
    // Zmodem帧类型
    public static final int ZPAD = '*';      // 填充字符
    public static final int ZDLE = 0x18;     // 转义字符
    public static final int ZDLEE = 0x58;    // 转义后的ZDLE
    public static final int ZBIN = 'A';      // 二进制帧
    public static final int ZHEX = 'B';      // 十六进制帧
    public static final int ZBIN32 = 'C';    // 32位CRC二进制帧
    
    // 帧类型
    public static final int ZRQINIT = 0;     // 请求初始化
    public static final int ZRINIT = 1;      // 接收初始化
    public static final int ZSINIT = 2;      // 发送初始化
    public static final int ZACK = 3;        // 确认
    public static final int ZFILE = 4;       // 文件头
    public static final int ZSKIP = 5;       // 跳过文件
    public static final int ZNAK = 6;        // 否定确认
    public static final int ZABORT = 7;      // 中止
    public static final int ZFIN = 8;        // 结束
    public static final int ZRPOS = 9;       // 恢复位置
    public static final int ZDATA = 10;      // 数据包
    public static final int ZEOF = 11;       // 文件结束
    public static final int ZFERR = 12;      // 文件错误
    public static final int ZCRC = 13;       // CRC请求
    public static final int ZCHALLENGE = 14; // 挑战
    public static final int ZCOMPL = 15;     // 完成
    public static final int ZCAN = 16;       // 取消
    public static final int ZFREECNT = 17;   // 可用空间
    public static final int ZCOMMAND = 18;   // 命令
    public static final int ZSTDERR = 19;    // 标准错误
    
    // 数据子包类型
    public static final int ZCRCW = 'a';     // CRC后等待
    public static final int ZCRCE = 'b';     // CRC结束
    public static final int ZCRCG = 'c';     // CRC继续
    public static final int ZCRCQ = 'd';     // CRC请求确认
    
    // ZRINIT标志
    public static final int CANFDX = 0x01;   // 全双工
    public static final int CANOVIO = 0x02;  // 可覆盖
    public static final int CANBRK = 0x04;   // 可发送BREAK
    public static final int CANCRY = 0x08;   // 可加密
    public static final int CANLZW = 0x10;   // 可LZW压缩
    public static final int CANFC32 = 0x20;  // 可32位CRC
    public static final int ESCCTL = 0x40;   // 转义控制字符
    public static final int ESC8 = 0x80;     // 转义8位字符
    
    // 特殊字符
    public static final int XON = 0x11;
    public static final int XOFF = 0x13;
    public static final int CAN = 0x18;
    
    // Zmodem检测序列
    public static final byte[] ZRQINIT_HEADER = {
        '*', '*', 0x18, 'B', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '\r', '\n'
    };
    
    // rz启动序列 (检测)
    public static final String RZ_START = "rz\r";
    public static final String RZ_WAITING = "rz waiting";
    
    // sz启动检测
    public static final byte[] SZ_SIGNATURE = { '*', '*', 0x18, 'B' };
    
    /**
     * 计算CRC16
     */
    public static int crc16(byte[] data, int offset, int length) {
        int crc = 0;
        for (int i = offset; i < offset + length; i++) {
            crc = updateCrc16(crc, data[i] & 0xFF);
        }
        return crc;
    }
    
    private static int updateCrc16(int crc, int c) {
        int[] crcTable = getCrc16Table();
        return (crcTable[(crc ^ c) & 0xFF] ^ (crc >> 8)) & 0xFFFF;
    }
    
    private static int[] getCrc16Table() {
        int[] table = new int[256];
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >> 1) ^ 0x8408;
                } else {
                    crc >>= 1;
                }
            }
            table[i] = crc;
        }
        return table;
    }
    
    /**
     * 计算CRC32
     */
    public static long crc32(byte[] data, int offset, int length) {
        long crc = 0xFFFFFFFFL;
        for (int i = offset; i < offset + length; i++) {
            crc = updateCrc32(crc, data[i] & 0xFF);
        }
        return crc ^ 0xFFFFFFFFL;
    }
    
    private static long updateCrc32(long crc, int c) {
        long[] crcTable = getCrc32Table();
        return crcTable[(int) ((crc ^ c) & 0xFF)] ^ (crc >> 8);
    }
    
    private static long[] getCrc32Table() {
        long[] table = new long[256];
        for (int i = 0; i < 256; i++) {
            long crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) != 0) {
                    crc = (crc >> 1) ^ 0xEDB88320L;
                } else {
                    crc >>= 1;
                }
            }
            table[i] = crc;
        }
        return table;
    }
    
    /**
     * 将字节转换为十六进制字符
     */
    public static char toHex(int value) {
        value &= 0x0F;
        return (char) (value < 10 ? '0' + value : 'a' + value - 10);
    }
    
    /**
     * 从十六进制字符转换
     */
    public static int fromHex(char c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }
}
