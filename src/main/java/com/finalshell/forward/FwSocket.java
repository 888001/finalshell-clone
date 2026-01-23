package com.finalshell.forward;

import java.io.*;
import java.net.Socket;

/**
 * 转发Socket
 * 封装Socket连接用于端口转发
 */
public class FwSocket {
    
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean closed;
    private long createTime;
    private long lastActiveTime;
    
    public FwSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.closed = false;
        this.createTime = System.currentTimeMillis();
        this.lastActiveTime = createTime;
    }
    
    public int read(byte[] buffer) throws IOException {
        int n = inputStream.read(buffer);
        if (n > 0) {
            lastActiveTime = System.currentTimeMillis();
        }
        return n;
    }
    
    public void write(byte[] data, int offset, int length) throws IOException {
        outputStream.write(data, offset, length);
        outputStream.flush();
        lastActiveTime = System.currentTimeMillis();
    }
    
    public void close() {
        if (closed) return;
        closed = true;
        try {
            if (inputStream != null) inputStream.close();
        } catch (Exception e) {}
        try {
            if (outputStream != null) outputStream.close();
        } catch (Exception e) {}
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {}
    }
    
    public boolean isClosed() {
        return closed || socket.isClosed();
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public long getLastActiveTime() {
        return lastActiveTime;
    }
    
    public long getIdleTime() {
        return System.currentTimeMillis() - lastActiveTime;
    }
}
