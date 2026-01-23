package com.finalshell.util;

/**
 * 标记工具类
 * 用于标记和追踪代码状态
 */
public class CMark {
    
    private static boolean marked = false;
    private static long markTime = 0;
    private static String markInfo = "";
    
    public static void mark() {
        marked = true;
        markTime = System.currentTimeMillis();
    }
    
    public static void mark(String info) {
        marked = true;
        markTime = System.currentTimeMillis();
        markInfo = info;
    }
    
    public static void unmark() {
        marked = false;
        markTime = 0;
        markInfo = "";
    }
    
    public static boolean isMarked() {
        return marked;
    }
    
    public static long getMarkTime() {
        return markTime;
    }
    
    public static String getMarkInfo() {
        return markInfo;
    }
}
