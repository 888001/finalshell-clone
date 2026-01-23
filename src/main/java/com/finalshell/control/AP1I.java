package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议接口1
 */
public interface AP1I {
    void authenticate(DataInputStream dis, DataOutputStream dos, String userName, String password) throws IOException;
}
