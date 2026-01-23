package com.finalshell.transfer;

import java.util.*;
import java.util.concurrent.*;

/**
 * 传输任务管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SFTP_Transfer_Analysis.md - TransTaskManager
 */
public class TransTaskManager {
    
    private static TransTaskManager instance;
    private List<TransTask> taskList = new CopyOnWriteArrayList<>();
    private ExecutorService executor;
    private int maxConcurrent = 3;
    private List<TransferListener> listeners = new ArrayList<>();
    
    private TransTaskManager() {
        executor = Executors.newFixedThreadPool(maxConcurrent);
    }
    
    public static synchronized TransTaskManager getInstance() {
        if (instance == null) {
            instance = new TransTaskManager();
        }
        return instance;
    }
    
    public void addTask(TransTask task) {
        taskList.add(task);
        fireTaskAdded(task);
        processQueue();
    }
    
    public void removeTask(TransTask task) {
        task.cancel();
        taskList.remove(task);
        fireTaskRemoved(task);
    }
    
    public void cancelTask(TransTask task) {
        task.cancel();
        fireTaskUpdated(task);
    }
    
    public void pauseTask(TransTask task) {
        task.pause();
        fireTaskUpdated(task);
    }
    
    public void resumeTask(TransTask task) {
        task.resume();
        processQueue();
        fireTaskUpdated(task);
    }
    
    private void processQueue() {
        int running = countRunning();
        
        for (TransTask task : taskList) {
            if (running >= maxConcurrent) break;
            
            if (task.getStatus() == TransTask.STATUS_WAITING) {
                executor.submit(() -> {
                    task.execute();
                    processQueue();
                });
                running++;
            }
        }
    }
    
    public int countRunning() {
        int count = 0;
        for (TransTask task : taskList) {
            if (task.getStatus() == TransTask.STATUS_RUNNING) {
                count++;
            }
        }
        return count;
    }
    
    public int countWaiting() {
        int count = 0;
        for (TransTask task : taskList) {
            if (task.getStatus() == TransTask.STATUS_WAITING) {
                count++;
            }
        }
        return count;
    }
    
    public int countWaitingAndRunning() {
        return countWaiting() + countRunning();
    }
    
    public TransTask getNextTask() {
        for (int i = taskList.size() - 1; i >= 0; i--) {
            TransTask task = taskList.get(i);
            if (task.getStatus() == TransTask.STATUS_WAITING) {
                return task;
            }
        }
        return null;
    }
    
    public void clearCompleted() {
        taskList.removeIf(task -> 
            task.getStatus() == TransTask.STATUS_SUCCESS ||
            task.getStatus() == TransTask.STATUS_ERROR ||
            task.getStatus() == TransTask.STATUS_CANCEL
        );
    }
    
    public void cancelAll() {
        for (TransTask task : taskList) {
            task.cancel();
        }
    }
    
    public List<TransTask> getTaskList() {
        return new ArrayList<>(taskList);
    }
    
    public void addListener(TransferListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(TransferListener listener) {
        listeners.remove(listener);
    }
    
    private void fireTaskAdded(TransTask task) {
        TransEvent event = new TransEvent(TransEvent.TYPE_START, task);
        for (TransferListener l : listeners) {
            l.onTransferEvent(event);
        }
    }
    
    private void fireTaskRemoved(TransTask task) {
        TransEvent event = new TransEvent(TransEvent.TYPE_CANCEL, task);
        for (TransferListener l : listeners) {
            l.onTransferEvent(event);
        }
    }
    
    private void fireTaskUpdated(TransTask task) {
        TransEvent event = new TransEvent(TransEvent.TYPE_PROGRESS, task);
        for (TransferListener l : listeners) {
            l.onTransferEvent(event);
        }
    }
    
    public void shutdown() {
        cancelAll();
        executor.shutdown();
    }
    
    public void setMaxConcurrent(int max) {
        this.maxConcurrent = max;
    }
}
