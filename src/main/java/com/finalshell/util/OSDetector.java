package com.finalshell.util;

/**
 * 操作系统检测工具类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Obfuscated_Subpackages_DeepAnalysis.md - OSDetector
 */
public class OSDetector {
    
    private static final boolean IS_WINDOWS;
    private static final boolean IS_LINUX;
    private static final boolean IS_MAC;
    private static final boolean IS_RPM;
    private static final boolean IS_DEB;
    private static final String OS_NAME;
    private static final String OS_VERSION;
    private static final String OS_ARCH;
    
    static {
        OS_NAME = System.getProperty("os.name", "").toLowerCase();
        OS_VERSION = System.getProperty("os.version", "");
        OS_ARCH = System.getProperty("os.arch", "");
        
        IS_WINDOWS = OS_NAME.contains("win");
        IS_LINUX = OS_NAME.contains("nux") || OS_NAME.contains("nix");
        IS_MAC = OS_NAME.contains("mac");
        
        boolean isDeb = false;
        boolean isRpm = false;
        
        if (IS_LINUX) {
            // 检测 Debian/Ubuntu 系
            try {
                Process p = Runtime.getRuntime().exec(new String[]{"which", "dpkg"});
                int exitCode = p.waitFor();
                isDeb = (exitCode == 0);
            } catch (Exception ignored) {}
            
            // 检测 RedHat/CentOS/Fedora 系
            if (!isDeb) {
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{"which", "rpm"});
                    int exitCode = p.waitFor();
                    isRpm = (exitCode == 0);
                } catch (Exception ignored) {}
            }
        }
        
        IS_DEB = isDeb;
        IS_RPM = isRpm;
    }
    
    private OSDetector() {}
    
    public static boolean isWindows() {
        return IS_WINDOWS;
    }
    
    public static boolean isLinux() {
        return IS_LINUX;
    }
    
    public static boolean isMac() {
        return IS_MAC;
    }
    
    public static boolean isUnix() {
        return IS_LINUX || IS_MAC;
    }
    
    public static boolean isRpm() {
        return IS_RPM;
    }
    
    public static boolean isDeb() {
        return IS_DEB;
    }
    
    public static String getOsName() {
        return OS_NAME;
    }
    
    public static String getOsVersion() {
        return OS_VERSION;
    }
    
    public static String getOsArch() {
        return OS_ARCH;
    }
    
    public static boolean is64Bit() {
        return OS_ARCH.contains("64");
    }
    
    public static boolean is32Bit() {
        return !is64Bit();
    }
    
    /**
     * 获取平台标识
     */
    public static String getPlatformId() {
        if (IS_WINDOWS) return "windows";
        if (IS_MAC) return "mac";
        if (IS_LINUX) return "linux";
        return "unknown";
    }
    
    /**
     * 获取用户主目录
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }
    
    /**
     * 获取临时目录
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }
    
    /**
     * 获取当前工作目录
     */
    public static String getCurrentDir() {
        return System.getProperty("user.dir");
    }
    
    /**
     * 获取系统行分隔符
     */
    public static String getLineSeparator() {
        return System.lineSeparator();
    }
    
    /**
     * 获取文件分隔符
     */
    public static String getFileSeparator() {
        return java.io.File.separator;
    }
    
    /**
     * 获取路径分隔符
     */
    public static String getPathSeparator() {
        return java.io.File.pathSeparator;
    }
}
