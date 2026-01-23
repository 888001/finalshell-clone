package com.finalshell.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 认证协议接口
 * 定义客户端认证的基本协议
 */
public interface AuthProtocol {
    
    /**
     * 执行认证
     * @param dis 数据输入流
     * @param dos 数据输出流
     * @param userName 用户名
     * @param password 密码
     * @throws IOException IO异常
     */
    void authenticate(DataInputStream dis, DataOutputStream dos, String userName, String password) throws IOException;
}
