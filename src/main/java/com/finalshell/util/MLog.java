package com.finalshell.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 简单日志工具
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: BatchClasses_Analysis.md - MLog
 */
public class MLog {
    
    public static final int LEVEL_DEBUG = 0;
    public static final int LEVEL_INFO = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_ERROR = 3;
    
    private static int logLevel = LEVEL_INFO;
    private static boolean writeToFile = false;
    private static String logFilePath = "finalshell.log";
    private static PrintWriter fileWriter;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    
    private MLog() {}
    
    public static void setLogLevel(int level) {
        logLevel = level;
    }
    
    public static void setWriteToFile(boolean write) {
        writeToFile = write;
        if (write && fileWriter == null) {
            try {
                fileWriter = new PrintWriter(new FileWriter(logFilePath, true), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void setLogFilePath(String path) {
        logFilePath = path;
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
        if (writeToFile) {
            setWriteToFile(true);
        }
    }
    
    public static void debug(String message) {
        log(LEVEL_DEBUG, "DEBUG", message, null);
    }
    
    public static void debug(String message, Throwable t) {
        log(LEVEL_DEBUG, "DEBUG", message, t);
    }
    
    public static void info(String message) {
        log(LEVEL_INFO, "INFO", message, null);
    }
    
    public static void info(String message, Throwable t) {
        log(LEVEL_INFO, "INFO", message, t);
    }
    
    public static void warn(String message) {
        log(LEVEL_WARN, "WARN", message, null);
    }
    
    public static void warn(String message, Throwable t) {
        log(LEVEL_WARN, "WARN", message, t);
    }
    
    public static void error(String message) {
        log(LEVEL_ERROR, "ERROR", message, null);
    }
    
    public static void error(String message, Throwable t) {
        log(LEVEL_ERROR, "ERROR", message, t);
    }
    
    private static void log(int level, String levelStr, String message, Throwable t) {
        if (level < logLevel) {
            return;
        }
        
        String timestamp = dateFormat.format(new Date());
        String threadName = Thread.currentThread().getName();
        String logMessage = String.format("[%s] [%s] [%s] %s", 
            timestamp, levelStr, threadName, message);
        
        PrintStream out = level >= LEVEL_ERROR ? System.err : System.out;
        out.println(logMessage);
        if (t != null) {
            t.printStackTrace(out);
        }
        
        if (writeToFile && fileWriter != null) {
            fileWriter.println(logMessage);
            if (t != null) {
                t.printStackTrace(fileWriter);
            }
        }
    }
    
    public static void close() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
    }
}
