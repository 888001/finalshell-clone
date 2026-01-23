package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议接口2
 */
public interface AP2I {
    void authenticate(DataInputStream dis, DataOutputStream dos) throws IOException;
}
