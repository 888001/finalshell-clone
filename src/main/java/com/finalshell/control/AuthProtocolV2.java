package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议V2接口
 * 无参数认证协议
 */
public interface AuthProtocolV2 {
    
    /**
     * 执行认证
     * @param dis 数据输入流
     * @param dos 数据输出流
     * @throws IOException IO异常
     */
    void authenticate(DataInputStream dis, DataOutputStream dos) throws IOException;
}
