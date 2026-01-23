package com.finalshell.util;

import java.util.concurrent.*;

/**
 * SSH工具类（线程池管理）
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: COMPLETE_SUMMARY.md - SshUtils
 */
public class SshUtils {
    
    private static ExecutorService executorService;
    private static ScheduledExecutorService scheduledExecutor;
    
    static {
        executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("SSH-Worker-" + t.getId());
            return t;
        });
        
        scheduledExecutor = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("SSH-Scheduled-" + t.getId());
            return t;
        });
    }
    
    private SshUtils() {}
    
    public static ExecutorService getExecutor() {
        return executorService;
    }
    
    public static ScheduledExecutorService getScheduledExecutor() {
        return scheduledExecutor;
    }
    
    public static void execute(Runnable task) {
        executorService.execute(task);
    }
    
    public static <T> Future<T> submit(Callable<T> task) {
        return executorService.submit(task);
    }
    
    public static Future<?> submit(Runnable task) {
        return executorService.submit(task);
    }
    
    public static ScheduledFuture<?> schedule(Runnable task, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(task, delay, unit);
    }
    
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, 
            long initialDelay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    
    public static void shutdown() {
        executorService.shutdown();
        scheduledExecutor.shutdown();
    }
    
    public static void shutdownNow() {
        executorService.shutdownNow();
        scheduledExecutor.shutdownNow();
    }
}
