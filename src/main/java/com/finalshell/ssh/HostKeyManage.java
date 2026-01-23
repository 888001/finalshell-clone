package com.finalshell.ssh;

/**
 * 主机密钥管理接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - HostKeyManage
 */
public interface HostKeyManage {
    
    /**
     * 验证主机密钥
     */
    boolean verifyHostKey(String host, int port, String keyType, byte[] key);
    
    /**
     * 添加主机密钥
     */
    void addHostKey(String host, int port, String keyType, byte[] key);
    
    /**
     * 删除主机密钥
     */
    void removeHostKey(String host, int port);
    
    /**
     * 检查主机密钥是否存在
     */
    boolean hasHostKey(String host, int port);
}
