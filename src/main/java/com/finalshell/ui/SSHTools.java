package com.finalshell.ui;

import com.finalshell.ssh.SSHSession;

import java.io.*;
import java.util.concurrent.*;

/**
 * SSH命令执行工具
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: COMPLETE_SUMMARY.md - SSHTools
 */
public class SSHTools {
    
    private static ExecutorService executor = Executors.newCachedThreadPool();
    
    private SSHTools() {}
    
    public static String executeCommand(SSHSession session, String command) throws Exception {
        return executeCommand(session, command, 30000);
    }
    
    public static String executeCommand(SSHSession session, String command, long timeout) throws Exception {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("SSH session is not connected");
        }
        
        Future<String> future = executor.submit(() -> {
            return session.executeCommand(command);
        });
        
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new Exception("Command execution timeout: " + command);
        }
    }
    
    public static void executeCommandAsync(SSHSession session, String command, 
            CommandCallback callback) {
        executor.submit(() -> {
            try {
                String result = session.executeCommand(command);
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
    
    public static boolean testConnection(SSHSession session) {
        try {
            String result = executeCommand(session, "echo test", 5000);
            return "test".equals(result.trim());
        } catch (Exception e) {
            return false;
        }
    }
    
    public static String getHomeDirectory(SSHSession session) throws Exception {
        return executeCommand(session, "echo $HOME").trim();
    }
    
    public static String getCurrentDirectory(SSHSession session) throws Exception {
        return executeCommand(session, "pwd").trim();
    }
    
    public static String getOSType(SSHSession session) throws Exception {
        return executeCommand(session, "uname -s").trim();
    }
    
    public static void shutdown() {
        executor.shutdown();
    }
    
    public interface CommandCallback {
        void onSuccess(String result);
        void onError(Exception e);
    }
}
