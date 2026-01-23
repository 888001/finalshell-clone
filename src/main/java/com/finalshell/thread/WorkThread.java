package com.finalshell.thread;

/**
 * 工作线程
 * 可控制的后台工作线程
 */
public class WorkThread extends Thread {
    
    private volatile boolean running;
    private volatile boolean paused;
    private Runnable task;
    private Object pauseLock;
    
    public WorkThread(Runnable task) {
        this(task, "WorkThread");
    }
    
    public WorkThread(Runnable task, String name) {
        super(name);
        this.task = task;
        this.running = true;
        this.paused = false;
        this.pauseLock = new Object();
    }
    
    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                while (paused && running) {
                    try {
                        pauseLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            
            if (!running) {
                break;
            }
            
            try {
                if (task != null) {
                    task.run();
                }
            } catch (Exception e) {
                onError(e);
            }
            
            running = false;
        }
    }
    
    public void pause() {
        paused = true;
    }
    
    public void resumeThread() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }
    
    public void stopThread() {
        running = false;
        resumeThread();
        interrupt();
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    protected void onError(Exception e) {
    }
}
