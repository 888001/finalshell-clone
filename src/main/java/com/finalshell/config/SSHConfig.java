package com.finalshell.config;

/**
 * SSH连接配置常量
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: BatchClasses_Analysis.md - SSHConfig
 */
public class SSHConfig {
    
    // 连接类型
    public static final int TYPE_SSH = 1;
    public static final int TYPE_SFTP = 2;
    public static final int TYPE_TELNET = 3;
    public static final int TYPE_RDP = 4;
    public static final int TYPE_VNC = 5;
    public static final int TYPE_SERIAL = 6;
    
    // 默认端口
    public static final int DEFAULT_SSH_PORT = 22;
    public static final int DEFAULT_TELNET_PORT = 23;
    public static final int DEFAULT_RDP_PORT = 3389;
    public static final int DEFAULT_VNC_PORT = 5900;
    
    // 认证方式
    public static final int AUTH_PASSWORD = 1;
    public static final int AUTH_PUBLICKEY = 2;
    public static final int AUTH_KEYBOARD_INTERACTIVE = 3;
    public static final int AUTH_GSSAPI = 4;
    
    // 密钥类型
    public static final String KEY_RSA = "RSA";
    public static final String KEY_DSA = "DSA";
    public static final String KEY_ECDSA = "ECDSA";
    public static final String KEY_ED25519 = "ED25519";
    
    // 加密算法
    public static final String[] CIPHERS = {
        "aes128-ctr", "aes192-ctr", "aes256-ctr",
        "aes128-gcm@openssh.com", "aes256-gcm@openssh.com",
        "chacha20-poly1305@openssh.com",
        "aes128-cbc", "aes192-cbc", "aes256-cbc",
        "3des-cbc", "blowfish-cbc"
    };
    
    // MAC算法
    public static final String[] MACS = {
        "hmac-sha2-256", "hmac-sha2-512",
        "hmac-sha1", "hmac-md5",
        "hmac-sha2-256-etm@openssh.com", "hmac-sha2-512-etm@openssh.com"
    };
    
    // 密钥交换算法
    public static final String[] KEX = {
        "curve25519-sha256", "curve25519-sha256@libssh.org",
        "ecdh-sha2-nistp256", "ecdh-sha2-nistp384", "ecdh-sha2-nistp521",
        "diffie-hellman-group-exchange-sha256",
        "diffie-hellman-group16-sha512", "diffie-hellman-group18-sha512",
        "diffie-hellman-group14-sha256", "diffie-hellman-group14-sha1"
    };
    
    // 终端类型
    public static final String TERM_XTERM = "xterm";
    public static final String TERM_XTERM_256COLOR = "xterm-256color";
    public static final String TERM_VT100 = "vt100";
    public static final String TERM_VT220 = "vt220";
    public static final String TERM_ANSI = "ansi";
    public static final String TERM_LINUX = "linux";
    
    // 默认终端设置
    public static final String DEFAULT_TERM = TERM_XTERM_256COLOR;
    public static final int DEFAULT_TERM_COLS = 80;
    public static final int DEFAULT_TERM_ROWS = 24;
    
    // 连接超时
    public static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 0;
    public static final int DEFAULT_KEEPALIVE_INTERVAL = 60;
    
    // 字符编码
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String[] ENCODINGS = {
        "UTF-8", "GBK", "GB2312", "GB18030", "BIG5",
        "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE"
    };
    
    private SSHConfig() {}
}
