package com.finalshell.codec;

import java.nio.*;
import java.nio.charset.*;

/**
 * 自定义字节解码器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - MyByteDecoder
 */
public class MyByteDecoder {
    
    private CharsetDecoder decoder;
    private ByteBuffer inputBuffer;
    private CharBuffer outputBuffer;
    private String charsetName;
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    
    public MyByteDecoder() {
        this("UTF-8");
    }
    
    public MyByteDecoder(String charsetName) {
        this.charsetName = charsetName;
        try {
            Charset charset = Charset.forName(charsetName);
            decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        } catch (Exception e) {
            Charset charset = Charset.forName("UTF-8");
            decoder = charset.newDecoder();
        }
        
        inputBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
        outputBuffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
    }
    
    public String decode(byte[] bytes) {
        return decode(bytes, 0, bytes.length);
    }
    
    public String decode(byte[] bytes, int offset, int length) {
        if (bytes == null || length == 0) {
            return "";
        }
        
        ByteBuffer input = ByteBuffer.wrap(bytes, offset, length);
        CharBuffer output = CharBuffer.allocate(length * 2);
        
        decoder.reset();
        CoderResult result = decoder.decode(input, output, true);
        decoder.flush(output);
        
        output.flip();
        return output.toString();
    }
    
    public void append(byte b) {
        if (!inputBuffer.hasRemaining()) {
            // 扩展缓冲区
            ByteBuffer newBuffer = ByteBuffer.allocate(inputBuffer.capacity() * 2);
            inputBuffer.flip();
            newBuffer.put(inputBuffer);
            inputBuffer = newBuffer;
        }
        inputBuffer.put(b);
    }
    
    public void append(byte[] bytes) {
        append(bytes, 0, bytes.length);
    }
    
    public void append(byte[] bytes, int offset, int length) {
        for (int i = 0; i < length; i++) {
            append(bytes[offset + i]);
        }
    }
    
    public String flush() {
        inputBuffer.flip();
        if (!inputBuffer.hasRemaining()) {
            inputBuffer.clear();
            return "";
        }
        
        outputBuffer.clear();
        decoder.reset();
        decoder.decode(inputBuffer, outputBuffer, true);
        decoder.flush(outputBuffer);
        
        outputBuffer.flip();
        String result = outputBuffer.toString();
        
        inputBuffer.clear();
        return result;
    }
    
    public void reset() {
        decoder.reset();
        inputBuffer.clear();
        outputBuffer.clear();
    }
    
    public void setCharset(String charsetName) {
        this.charsetName = charsetName;
        try {
            Charset charset = Charset.forName(charsetName);
            decoder = charset.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        } catch (Exception e) {
            // 保持当前解码器
        }
    }
    
    public String getCharsetName() {
        return charsetName;
    }
}
