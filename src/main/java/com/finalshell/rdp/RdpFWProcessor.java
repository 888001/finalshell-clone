package com.finalshell.rdp;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RDP端口转发处理器
 * 处理RDP连接的端口转发
 */
public class RdpFWProcessor implements Runnable {
    
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private ServerSocket serverSocket;
    private AtomicBoolean running;
    private RdpFWListener listener;
    
    public RdpFWProcessor(int localPort, String remoteHost, int remotePort) {
        this.localPort = localPort;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
        this.running = new AtomicBoolean(false);
    }
    
    public void start() throws IOException {
        if (running.get()) return;
        
        serverSocket = new ServerSocket(localPort);
        running.set(true);
        
        Thread thread = new Thread(this, "RdpFWProcessor-" + localPort);
        thread.setDaemon(true);
        thread.start();
        
        if (listener != null) {
            listener.onStarted(localPort);
        }
    }
    
    public void stop() {
        running.set(false);
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {}
        
        if (listener != null) {
            listener.onStopped();
        }
    }
    
    @Override
    public void run() {
        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleConnection(clientSocket);
            } catch (IOException e) {
                if (running.get()) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        }
    }
    
    private void handleConnection(Socket clientSocket) {
        Thread thread = new Thread(() -> {
            try (Socket remoteSocket = new Socket(remoteHost, remotePort)) {
                Thread t1 = startForward(clientSocket.getInputStream(), remoteSocket.getOutputStream());
                Thread t2 = startForward(remoteSocket.getInputStream(), clientSocket.getOutputStream());
                
                t1.join();
                t2.join();
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e);
                }
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {}
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    
    private Thread startForward(InputStream in, OutputStream out) {
        Thread thread = new Thread(() -> {
            byte[] buffer = new byte[8192];
            int n;
            try {
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                    out.flush();
                }
            } catch (IOException e) {}
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    public int getLocalPort() {
        return localPort;
    }
    
    public void setListener(RdpFWListener listener) {
        this.listener = listener;
    }
    
    public interface RdpFWListener {
        void onStarted(int port);
        void onStopped();
        void onError(Exception e);
    }
}
