package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议实现1
 */
public class AP1 implements AP1I {
    
    @Override
    public void authenticate(DataInputStream dis, DataOutputStream dos, String userName, String password) throws IOException {
        dos.writeInt(userName.getBytes().length);
        dos.write(userName.getBytes());
        dos.writeInt(password.getBytes().length);
        dos.write(password.getBytes());
    }
}
