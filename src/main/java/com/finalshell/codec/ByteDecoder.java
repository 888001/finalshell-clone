package com.finalshell.codec;

import java.nio.*;
import java.nio.charset.*;

/**
 * 自定义UTF-8字节解码器 - 支持多字节字符和代理对
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: ByteDecoder_StreamDecoder_DeepAnalysis.md
 */
public class ByteDecoder extends CharsetDecoder {
    
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    
    public ByteDecoder() {
        super(UTF8, 1.0f, 1.0f);
    }
    
    @Override
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        if (in.hasArray() && out.hasArray()) {
            return decodeArrayLoop(in, out);
        } else {
            return decodeBufferLoop(in, out);
        }
    }
    
    private CoderResult decodeArrayLoop(ByteBuffer src, CharBuffer dst) {
        byte[] sa = src.array();
        int sp = src.arrayOffset() + src.position();
        int sl = src.arrayOffset() + src.limit();
        
        char[] da = dst.array();
        int dp = dst.arrayOffset() + dst.position();
        int dl = dst.arrayOffset() + dst.limit();
        
        try {
            while (sp < sl) {
                int b1 = sa[sp];
                
                if (b1 >= 0) {
                    // ASCII (单字节)
                    if (dp >= dl) return CoderResult.OVERFLOW;
                    da[dp++] = (char) b1;
                    sp++;
                } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                    // 2字节序列
                    if (sl - sp < 2) return CoderResult.UNDERFLOW;
                    int b2 = sa[sp + 1];
                    if (isNotContinuation(b2)) return CoderResult.malformedForLength(1);
                    if (dp >= dl) return CoderResult.OVERFLOW;
                    da[dp++] = (char) (((b1 << 6) ^ b2) ^ 0x0f80);
                    sp += 2;
                } else if ((b1 >> 4) == -2) {
                    // 3字节序列
                    if (sl - sp < 3) return CoderResult.UNDERFLOW;
                    int b2 = sa[sp + 1];
                    int b3 = sa[sp + 2];
                    if (isMalformed3(b1, b2, b3)) return CoderResult.malformedForLength(1);
                    if (dp >= dl) return CoderResult.OVERFLOW;
                    char c = (char) ((b1 << 12) ^ (b2 << 6) ^ (b3 ^ 0x1f80));
                    if (Character.isSurrogate(c)) return CoderResult.malformedForLength(3);
                    da[dp++] = c;
                    sp += 3;
                } else if ((b1 >> 3) == -2) {
                    // 4字节序列 (代理对)
                    if (sl - sp < 4) return CoderResult.UNDERFLOW;
                    int b2 = sa[sp + 1];
                    int b3 = sa[sp + 2];
                    int b4 = sa[sp + 3];
                    if (isMalformed4(b2, b3, b4)) return CoderResult.malformedForLength(1);
                    int uc = ((b1 << 18) ^ (b2 << 12) ^ (b3 << 6) ^ (b4 ^ 0x381f80));
                    if (!Character.isSupplementaryCodePoint(uc)) return CoderResult.malformedForLength(4);
                    if (dl - dp < 2) return CoderResult.OVERFLOW;
                    da[dp++] = Character.highSurrogate(uc);
                    da[dp++] = Character.lowSurrogate(uc);
                    sp += 4;
                } else {
                    return CoderResult.malformedForLength(1);
                }
            }
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(sp - src.arrayOffset());
            dst.position(dp - dst.arrayOffset());
        }
    }
    
    private CoderResult decodeBufferLoop(ByteBuffer src, CharBuffer dst) {
        int mark = src.position();
        try {
            while (src.hasRemaining()) {
                int b1 = src.get();
                
                if (b1 >= 0) {
                    if (!dst.hasRemaining()) return CoderResult.OVERFLOW;
                    dst.put((char) b1);
                    mark++;
                } else if ((b1 >> 5) == -2 && (b1 & 0x1e) != 0) {
                    if (src.remaining() < 1) return CoderResult.UNDERFLOW;
                    int b2 = src.get();
                    if (isNotContinuation(b2)) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (!dst.hasRemaining()) return CoderResult.OVERFLOW;
                    dst.put((char) (((b1 << 6) ^ b2) ^ 0x0f80));
                    mark += 2;
                } else if ((b1 >> 4) == -2) {
                    if (src.remaining() < 2) return CoderResult.UNDERFLOW;
                    int b2 = src.get();
                    int b3 = src.get();
                    if (isMalformed3(b1, b2, b3)) {
                        return CoderResult.malformedForLength(1);
                    }
                    if (!dst.hasRemaining()) return CoderResult.OVERFLOW;
                    char c = (char) ((b1 << 12) ^ (b2 << 6) ^ (b3 ^ 0x1f80));
                    if (Character.isSurrogate(c)) {
                        return CoderResult.malformedForLength(3);
                    }
                    dst.put(c);
                    mark += 3;
                } else if ((b1 >> 3) == -2) {
                    if (src.remaining() < 3) return CoderResult.UNDERFLOW;
                    int b2 = src.get();
                    int b3 = src.get();
                    int b4 = src.get();
                    if (isMalformed4(b2, b3, b4)) {
                        return CoderResult.malformedForLength(1);
                    }
                    int uc = ((b1 << 18) ^ (b2 << 12) ^ (b3 << 6) ^ (b4 ^ 0x381f80));
                    if (!Character.isSupplementaryCodePoint(uc)) {
                        return CoderResult.malformedForLength(4);
                    }
                    if (dst.remaining() < 2) return CoderResult.OVERFLOW;
                    dst.put(Character.highSurrogate(uc));
                    dst.put(Character.lowSurrogate(uc));
                    mark += 4;
                } else {
                    return CoderResult.malformedForLength(1);
                }
            }
            return CoderResult.UNDERFLOW;
        } finally {
            src.position(mark);
        }
    }
    
    private static boolean isNotContinuation(int b) {
        return (b & 0xC0) != 0x80;
    }
    
    private static boolean isMalformed3(int b1, int b2, int b3) {
        return (b1 == -32 && (b2 & 0xE0) == 0x80) || 
               (b2 & 0xC0) != 0x80 || 
               (b3 & 0xC0) != 0x80;
    }
    
    private static boolean isMalformed4(int b2, int b3, int b4) {
        return (b2 & 0xC0) != 0x80 || 
               (b3 & 0xC0) != 0x80 || 
               (b4 & 0xC0) != 0x80;
    }
    
    /**
     * 检测Zmodem传输开始标记
     */
    public static boolean containsZmodemStart(byte[] data, int offset, int length) {
        // ZRQINIT: **\x18B00
        byte[] zmodemMarker = {0x2A, 0x2A, 0x18, 0x42, 0x30, 0x30};
        
        for (int i = offset; i <= offset + length - zmodemMarker.length; i++) {
            boolean match = true;
            for (int j = 0; j < zmodemMarker.length; j++) {
                if (data[i + j] != zmodemMarker[j]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }
}
