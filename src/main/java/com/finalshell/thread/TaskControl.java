package com.finalshell.thread;

/**
 * 任务控制器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: ThreadManager_Mail_DeepAnalysis.md - TaskControl
 */
public class TaskControl {
    
    public static final int TYPE_ONCE = 1;
    public static final int TYPE_LOOP = 2;
    
    private final Runnable runnable;
    private final int type;
    private int delay;
    private long scheduleTime;
    private volatile boolean running = false;
    private volatile boolean cancelled = false;
    private Thread thread;
    
    public TaskControl(Runnable runnable, int type) {
        this.runnable = runnable;
        this.type = type;
    }
    
    /**
     * 取消任务
     */
    public void cancel() {
        this.cancelled = true;
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    // Getters and Setters
    public Runnable getRunnable() { return runnable; }
    public int getType() { return type; }
    
    public int getDelay() { return delay; }
    public void setDelay(int delay) { this.delay = delay; }
    
    public long getScheduleTime() { return scheduleTime; }
    public void setScheduleTime(long scheduleTime) { this.scheduleTime = scheduleTime; }
    
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
    
    public boolean isCancelled() { return cancelled; }
    
    public Thread getThread() { return thread; }
    public void setThread(Thread thread) { this.thread = thread; }
}
