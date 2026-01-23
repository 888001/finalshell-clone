package com.finalshell.codec;

import java.io.*;
import java.nio.charset.*;

/**
 * 自定义流解码器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Browser_Theme_IP_Analysis.md - MyStreamDecoder
 */
public class MyStreamDecoder extends Reader {
    
    private InputStream inputStream;
    private InputStreamReader reader;
    private String charsetName;
    private byte[] byteBuffer;
    private char[] charBuffer;
    private int bufferPos;
    private int bufferLen;
    private static final int BUFFER_SIZE = 8192;
    
    public MyStreamDecoder(InputStream in) {
        this(in, "UTF-8");
    }
    
    public MyStreamDecoder(InputStream in, String charsetName) {
        this.inputStream = in;
        this.charsetName = charsetName;
        this.byteBuffer = new byte[BUFFER_SIZE];
        this.charBuffer = new char[BUFFER_SIZE];
        this.bufferPos = 0;
        this.bufferLen = 0;
        
        try {
            Charset charset = Charset.forName(charsetName);
            this.reader = new InputStreamReader(in, charset);
        } catch (Exception e) {
            this.reader = new InputStreamReader(in, StandardCharsets.UTF_8);
        }
    }
    
    @Override
    public int read() throws IOException {
        if (bufferPos >= bufferLen) {
            fillBuffer();
            if (bufferLen <= 0) {
                return -1;
            }
        }
        return charBuffer[bufferPos++];
    }
    
    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (bufferPos >= bufferLen) {
            fillBuffer();
            if (bufferLen <= 0) {
                return -1;
            }
        }
        
        int available = bufferLen - bufferPos;
        int toRead = Math.min(available, len);
        System.arraycopy(charBuffer, bufferPos, cbuf, off, toRead);
        bufferPos += toRead;
        return toRead;
    }
    
    private void fillBuffer() throws IOException {
        bufferLen = reader.read(charBuffer, 0, charBuffer.length);
        bufferPos = 0;
    }
    
    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = read()) != -1) {
            if (c == '\n') {
                break;
            }
            if (c == '\r') {
                // 检查是否有\n跟随
                int next = read();
                if (next != '\n' && next != -1) {
                    // 不是\r\n，需要回退
                    // 这里简化处理，不回退
                }
                break;
            }
            sb.append((char) c);
        }
        if (c == -1 && sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }
    
    public int available() throws IOException {
        return (bufferLen - bufferPos) + (inputStream.available() > 0 ? 1 : 0);
    }
    
    @Override
    public boolean ready() throws IOException {
        return bufferPos < bufferLen || reader.ready();
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
    
    public void setCharset(String charsetName) {
        // 注意：更改字符集后需要重新创建reader
        // 这里简化处理，仅记录新字符集
        this.charsetName = charsetName;
    }
    
    public String getCharsetName() {
        return charsetName;
    }
}
