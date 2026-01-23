package com.finalshell.control;

/**
 * FinalShell协议常量
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Control_Auth_DeepAnalysis.md - FSProtocol
 */
public class FSProtocol {
    
    // 返回码
    public static final int SUCCESS = 0;
    public static final int ERROR = -1;
    public static final int INVALID_USER = 1;
    public static final int INVALID_PASSWORD = 2;
    public static final int EXPIRED = 3;
    public static final int DEVICE_LIMIT = 4;
    public static final int SERVER_ERROR = 5;
    public static final int NETWORK_ERROR = 6;
    
    // 命令
    public static final String CMD_LOGIN = "login";
    public static final String CMD_LOGOUT = "logout";
    public static final String CMD_CHECK = "check";
    public static final String CMD_SYNC = "sync";
    public static final String CMD_UPDATE = "update";
    
    // 魔数
    public static final long MAGIC_NUMBER = 86264145L;
    
    // 版本
    public static final int VERSION_NUM = 179;
    
    private FSProtocol() {}
}
