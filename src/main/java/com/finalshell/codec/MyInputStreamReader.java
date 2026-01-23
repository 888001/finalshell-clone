package com.finalshell.codec;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * 自定义输入流读取器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - MyInputStreamReader
 */
public class MyInputStreamReader extends Reader {
    
    private InputStream inputStream;
    private CharsetDecoder decoder;
    private ByteBuffer byteBuffer;
    private CharBuffer charBuffer;
    private String charsetName;
    private boolean endOfInput = false;
    
    private static final int BYTE_BUFFER_SIZE = 8192;
    private static final int CHAR_BUFFER_SIZE = 8192;
    
    public MyInputStreamReader(InputStream in) {
        this(in, "UTF-8");
    }
    
    public MyInputStreamReader(InputStream in, String charsetName) {
        this.inputStream = in;
        this.charsetName = charsetName;
        
        try {
            Charset charset = Charset.forName(charsetName);
            this.decoder = charset.newDecoder();
            this.decoder.onMalformedInput(CodingErrorAction.REPLACE);
            this.decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        } catch (Exception e) {
            Charset charset = StandardCharsets.UTF_8;
            this.decoder = charset.newDecoder();
        }
        
        this.byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_SIZE);
        this.byteBuffer.flip(); // 准备读取
        this.charBuffer = CharBuffer.allocate(CHAR_BUFFER_SIZE);
        this.charBuffer.flip(); // 准备读取
    }
    
    @Override
    public int read() throws IOException {
        if (!charBuffer.hasRemaining()) {
            if (!fillCharBuffer()) {
                return -1;
            }
        }
        return charBuffer.get();
    }
    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        
        int totalRead = 0;
        while (totalRead < len) {
            if (!charBuffer.hasRemaining()) {
                if (!fillCharBuffer()) {
                    break;
                }
            }
            
            int remaining = charBuffer.remaining();
            int toRead = Math.min(remaining, len - totalRead);
            charBuffer.get(cbuf, off + totalRead, toRead);
            totalRead += toRead;
        }
        
        return totalRead > 0 ? totalRead : -1;
    }
    
    private boolean fillCharBuffer() throws IOException {
        charBuffer.clear();
        
        while (true) {
            // 尝试解码
            CoderResult result = decoder.decode(byteBuffer, charBuffer, endOfInput);
            
            if (charBuffer.position() > 0) {
                charBuffer.flip();
                return true;
            }
            
            if (result.isUnderflow()) {
                if (endOfInput) {
                    // 刷新解码器
                    decoder.flush(charBuffer);
                    if (charBuffer.position() > 0) {
                        charBuffer.flip();
                        return true;
                    }
                    return false;
                }
                
                // 需要更多输入
                if (!fillByteBuffer()) {
                    endOfInput = true;
                }
            } else if (result.isOverflow()) {
                // 输出缓冲区满
                charBuffer.flip();
                return true;
            } else {
                result.throwException();
            }
        }
    }
    
    private boolean fillByteBuffer() throws IOException {
        byteBuffer.compact();
        
        int read = inputStream.read(byteBuffer.array(), byteBuffer.position(), 
            byteBuffer.remaining());
        
        if (read <= 0) {
            byteBuffer.flip();
            return false;
        }
        
        byteBuffer.position(byteBuffer.position() + read);
        byteBuffer.flip();
        return true;
    }
    
    @Override
    public boolean ready() throws IOException {
        return charBuffer.hasRemaining() || inputStream.available() > 0;
    }
    
    @Override
    public void close() throws IOException {
        inputStream.close();
    }
    
    public String getCharsetName() {
        return charsetName;
    }
}
