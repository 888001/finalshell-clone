package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议V1实现
 * 基本用户名密码认证
 */
public class AuthProtocolV1 implements AuthProtocol {
    
    @Override
    public void authenticate(DataInputStream dis, DataOutputStream dos, String userName, String password) throws IOException {
        byte[] userBytes = userName.getBytes("UTF-8");
        byte[] passBytes = password.getBytes("UTF-8");
        
        dos.writeInt(userBytes.length);
        dos.write(userBytes);
        dos.writeInt(passBytes.length);
        dos.write(passBytes);
        dos.flush();
    }
}
