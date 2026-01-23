package com.finalshell.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 线程管理器 - 任务调度
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: ThreadManager_Mail_DeepAnalysis.md - ThreadManager
 */
public class ThreadManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadManager.class);
    
    private Thread schedulerThread;
    private final List<TaskControl> taskList = new ArrayList<>();
    private final List<TaskControl> removeList = new ArrayList<>();
    private ThreadPoolExecutor executor;
    private volatile boolean running = true;
    
    private static ThreadManager instance;
    
    private ThreadManager() {
        // 创建无界线程池
        SynchronousQueue<Runnable> queue = new SynchronousQueue<>();
        this.executor = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            10000L,
            TimeUnit.MILLISECONDS,
            queue
        );
        
        // 启动调度线程
        this.schedulerThread = new Thread(this::schedulerLoop, "TaskScheduler");
        this.schedulerThread.setDaemon(true);
        this.schedulerThread.start();
        
        logger.info("线程管理器已启动");
    }
    
    public static synchronized ThreadManager getInstance() {
        if (instance == null) {
            instance = new ThreadManager();
        }
        return instance;
    }
    
    /**
     * 调度循环
     */
    private void schedulerLoop() {
        while (running) {
            checkAndExecute();
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * 检查并执行任务
     */
    private synchronized void checkAndExecute() {
        removeList.clear();
        
        for (TaskControl task : taskList) {
            if (task.isRunning()) continue;
            if (System.currentTimeMillis() < task.getScheduleTime()) continue;
            
            if (task.getType() == TaskControl.TYPE_ONCE) {
                removeList.add(task);
            }
            
            if (task.isCancelled()) {
                removeList.add(task);
                continue;
            }
            
            task.setRunning(true);
            executor.execute(() -> {
                try {
                    task.getRunnable().run();
                } catch (Exception e) {
                    logger.error("任务执行异常", e);
                } finally {
                    if (!task.isCancelled() && task.getType() == TaskControl.TYPE_LOOP) {
                        task.setScheduleTime(System.currentTimeMillis() + task.getDelay());
                        task.setRunning(false);
                    }
                }
            });
        }
        
        taskList.removeAll(removeList);
    }
    
    /**
     * 立即执行
     */
    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }
    
    /**
     * 提交任务
     */
    public <T> Future<T> submit(Callable<T> callable) {
        return executor.submit(callable);
    }
    
    /**
     * 延迟执行(一次性)
     */
    public synchronized TaskControl execDelay(Runnable runnable, int delayMs) {
        TaskControl task = new TaskControl(runnable, TaskControl.TYPE_ONCE);
        task.setDelay(delayMs);
        task.setScheduleTime(System.currentTimeMillis() + delayMs);
        taskList.add(task);
        return task;
    }
    
    /**
     * 循环执行
     */
    public synchronized TaskControl execLoop(Runnable runnable, int delayMs) {
        TaskControl task = new TaskControl(runnable, TaskControl.TYPE_LOOP);
        task.setDelay(delayMs);
        task.setScheduleTime(System.currentTimeMillis() + delayMs);
        taskList.add(task);
        return task;
    }
    
    /**
     * 取消任务
     */
    public synchronized void cancel(TaskControl task) {
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * 停止管理器
     */
    public void shutdown() {
        running = false;
        if (schedulerThread != null) {
            schedulerThread.interrupt();
        }
        if (executor != null) {
            executor.shutdown();
        }
        logger.info("线程管理器已停止");
    }
    
    /**
     * 获取活跃线程数
     */
    public int getActiveCount() {
        return executor.getActiveCount();
    }
    
    /**
     * 获取任务数
     */
    public int getTaskCount() {
        return taskList.size();
    }
}
