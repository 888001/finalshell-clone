package com.finalshell.util;

/**
 * 命令行工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CL {
    
    public static final String LINUX_CPU = "top -bn1 | head -5";
    public static final String LINUX_MEM = "free -b";
    public static final String LINUX_DISK = "df -h";
    public static final String LINUX_NET = "cat /proc/net/dev";
    public static final String LINUX_PS = "ps aux --sort=-%cpu | head -20";
    public static final String LINUX_UPTIME = "uptime";
    public static final String LINUX_WHO = "who";
    public static final String LINUX_UNAME = "uname -a";
    
    public static final String MAC_CPU = "top -l 1 | head -10";
    public static final String MAC_MEM = "vm_stat";
    public static final String MAC_DISK = "df -h";
    public static final String MAC_NET = "netstat -ib";
    public static final String MAC_PS = "ps aux | sort -nrk 3 | head -20";
    
    private CL() {
    }
    
    public static String getCpuCommand(String osType) {
        if ("mac".equalsIgnoreCase(osType) || "darwin".equalsIgnoreCase(osType)) {
            return MAC_CPU;
        }
        return LINUX_CPU;
    }
    
    public static String getMemCommand(String osType) {
        if ("mac".equalsIgnoreCase(osType) || "darwin".equalsIgnoreCase(osType)) {
            return MAC_MEM;
        }
        return LINUX_MEM;
    }
    
    public static String getDiskCommand(String osType) {
        return LINUX_DISK;
    }
    
    public static String getNetCommand(String osType) {
        if ("mac".equalsIgnoreCase(osType) || "darwin".equalsIgnoreCase(osType)) {
            return MAC_NET;
        }
        return LINUX_NET;
    }
    
    public static String getPsCommand(String osType) {
        if ("mac".equalsIgnoreCase(osType) || "darwin".equalsIgnoreCase(osType)) {
            return MAC_PS;
        }
        return LINUX_PS;
    }
}
