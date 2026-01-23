package com.finalshell.codec;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * 自定义流解码器 - 用于终端数据流处理
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: ByteDecoder_StreamDecoder_DeepAnalysis.md
 */
public class StreamDecoder extends Reader {
    
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    
    private final InputStream in;
    private final CharsetDecoder decoder;
    private final ByteBuffer byteBuf;
    private final CharBuffer charBuf;
    
    private boolean eof = false;
    private boolean closed = false;
    
    // Zmodem检测状态
    private boolean zmodemDetected = false;
    private ZmodemCallback zmodemCallback;
    
    public StreamDecoder(InputStream in, Charset charset) {
        this(in, charset, DEFAULT_BUFFER_SIZE);
    }
    
    public StreamDecoder(InputStream in, Charset charset, int bufferSize) {
        this.in = in;
        this.decoder = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.byteBuf = ByteBuffer.allocate(bufferSize);
        this.charBuf = CharBuffer.allocate(bufferSize);
        byteBuf.flip();
        charBuf.flip();
    }
    
    public void setZmodemCallback(ZmodemCallback callback) {
        this.zmodemCallback = callback;
    }
    
    @Override
    public int read() throws IOException {
        char[] cb = new char[1];
        return read(cb, 0, 1) == -1 ? -1 : cb[0];
    }
    
    @Override
    public int read(char[] cbuf, int offset, int length) throws IOException {
        ensureOpen();
        
        if (length == 0) return 0;
        
        int n = 0;
        
        // 首先从字符缓冲区读取
        if (charBuf.hasRemaining()) {
            n = Math.min(length, charBuf.remaining());
            charBuf.get(cbuf, offset, n);
            if (n == length) return n;
            offset += n;
            length -= n;
        }
        
        // 需要更多数据
        while (length > 0) {
            // 解码更多字符
            charBuf.clear();
            CoderResult result = decodeMore();
            charBuf.flip();
            
            if (charBuf.hasRemaining()) {
                int count = Math.min(length, charBuf.remaining());
                charBuf.get(cbuf, offset, count);
                n += count;
                offset += count;
                length -= count;
            }
            
            if (result == CoderResult.UNDERFLOW && eof) {
                return n > 0 ? n : -1;
            }
        }
        
        return n;
    }
    
    private CoderResult decodeMore() throws IOException {
        // 填充字节缓冲区
        if (!byteBuf.hasRemaining() && !eof) {
            byteBuf.clear();
            int n = fillBuffer();
            byteBuf.flip();
            if (n == -1) {
                eof = true;
            }
        }
        
        // 解码
        return decoder.decode(byteBuf, charBuf, eof);
    }
    
    private int fillBuffer() throws IOException {
        byte[] arr = byteBuf.array();
        int pos = byteBuf.position();
        int rem = byteBuf.remaining();
        
        int n = in.read(arr, pos, rem);
        
        // Zmodem检测
        if (n > 0 && zmodemCallback != null && !zmodemDetected) {
            if (ByteDecoder.containsZmodemStart(arr, pos, n)) {
                zmodemDetected = true;
                zmodemCallback.onZmodemDetected();
            }
        }
        
        return n;
    }
    
    public void resetZmodemDetection() {
        zmodemDetected = false;
    }
    
    public boolean isZmodemDetected() {
        return zmodemDetected;
    }
    
    @Override
    public boolean ready() throws IOException {
        ensureOpen();
        return charBuf.hasRemaining() || in.available() > 0;
    }
    
    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            in.close();
        }
    }
    
    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
    }
    
    /**
     * Zmodem检测回调接口
     */
    public interface ZmodemCallback {
        void onZmodemDetected();
    }
}
