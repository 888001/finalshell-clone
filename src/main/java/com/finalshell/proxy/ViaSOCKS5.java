package com.finalshell.proxy;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * SOCKS5代理连接工具
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ViaSOCKS5 {
    
    private String proxyHost;
    private int proxyPort;
    private String username;
    private String password;
    
    public ViaSOCKS5(String proxyHost, int proxyPort) {
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }
    
    public void setAuth(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public Socket connect(String targetHost, int targetPort) throws Exception {
        Socket socket = new Socket(proxyHost, proxyPort);
        
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        
        // SOCKS5 握手
        byte[] greeting;
        if (username != null && !username.isEmpty()) {
            greeting = new byte[]{0x05, 0x02, 0x00, 0x02}; // 支持无认证和用户名密码
        } else {
            greeting = new byte[]{0x05, 0x01, 0x00}; // 仅支持无认证
        }
        out.write(greeting);
        out.flush();
        
        byte[] response = new byte[2];
        in.read(response);
        
        if (response[0] != 0x05) {
            socket.close();
            throw new Exception("SOCKS5 版本不匹配");
        }
        
        // 用户名密码认证
        if (response[1] == 0x02) {
            byte[] auth = buildAuthPacket();
            out.write(auth);
            out.flush();
            
            byte[] authResponse = new byte[2];
            in.read(authResponse);
            if (authResponse[1] != 0x00) {
                socket.close();
                throw new Exception("SOCKS5 认证失败");
            }
        }
        
        // 发送连接请求
        byte[] connectRequest = buildConnectRequest(targetHost, targetPort);
        out.write(connectRequest);
        out.flush();
        
        byte[] connectResponse = new byte[10];
        in.read(connectResponse);
        
        if (connectResponse[1] != 0x00) {
            socket.close();
            throw new Exception("SOCKS5 连接失败: " + connectResponse[1]);
        }
        
        return socket;
    }
    
    private byte[] buildAuthPacket() {
        byte[] usernameBytes = username.getBytes();
        byte[] passwordBytes = password.getBytes();
        byte[] packet = new byte[3 + usernameBytes.length + passwordBytes.length];
        packet[0] = 0x01;
        packet[1] = (byte) usernameBytes.length;
        System.arraycopy(usernameBytes, 0, packet, 2, usernameBytes.length);
        packet[2 + usernameBytes.length] = (byte) passwordBytes.length;
        System.arraycopy(passwordBytes, 0, packet, 3 + usernameBytes.length, passwordBytes.length);
        return packet;
    }
    
    private byte[] buildConnectRequest(String host, int port) {
        byte[] hostBytes = host.getBytes();
        byte[] request = new byte[7 + hostBytes.length];
        request[0] = 0x05; // SOCKS5
        request[1] = 0x01; // CONNECT
        request[2] = 0x00; // Reserved
        request[3] = 0x03; // Domain name
        request[4] = (byte) hostBytes.length;
        System.arraycopy(hostBytes, 0, request, 5, hostBytes.length);
        request[5 + hostBytes.length] = (byte) ((port >> 8) & 0xFF);
        request[6 + hostBytes.length] = (byte) (port & 0xFF);
        return request;
    }
}
