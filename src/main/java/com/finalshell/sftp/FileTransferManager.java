package com.finalshell.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * File Transfer Manager - Manages file transfer queue and progress
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md
 */
public class FileTransferManager {
    
    private static final Logger logger = LoggerFactory.getLogger(FileTransferManager.class);
    
    private static FileTransferManager instance;
    
    private final ExecutorService executor;
    private final Map<String, TransferTask> activeTasks = new ConcurrentHashMap<>();
    private final List<TransferTask> pendingTasks = new CopyOnWriteArrayList<>();
    private final List<TransferTask> completedTasks = new CopyOnWriteArrayList<>();
    private final List<TransferListener> listeners = new CopyOnWriteArrayList<>();
    
    private int maxConcurrentTransfers = 3;
    
    public static FileTransferManager getInstance() {
        if (instance == null) {
            instance = new FileTransferManager();
        }
        return instance;
    }
    
    private FileTransferManager() {
        executor = Executors.newFixedThreadPool(maxConcurrentTransfers);
    }
    
    /**
     * Add download task
     */
    public TransferTask addDownload(SFTPSession session, String remotePath, String localPath) {
        TransferTask task = new TransferTask(
            UUID.randomUUID().toString(),
            TransferTask.Type.DOWNLOAD,
            remotePath,
            localPath,
            session
        );
        
        pendingTasks.add(task);
        processQueue();
        fireEvent(TransferEvent.TASK_ADDED, task);
        
        logger.info("Download task added: {} -> {}", remotePath, localPath);
        return task;
    }
    
    /**
     * Add upload task
     */
    public TransferTask addUpload(SFTPSession session, String localPath, String remotePath) {
        TransferTask task = new TransferTask(
            UUID.randomUUID().toString(),
            TransferTask.Type.UPLOAD,
            localPath,
            remotePath,
            session
        );
        
        File localFile = new File(localPath);
        if (localFile.exists()) {
            task.setTotalSize(localFile.length());
        }
        
        pendingTasks.add(task);
        processQueue();
        fireEvent(TransferEvent.TASK_ADDED, task);
        
        logger.info("Upload task added: {} -> {}", localPath, remotePath);
        return task;
    }
    
    /**
     * Process pending queue
     */
    private synchronized void processQueue() {
        while (activeTasks.size() < maxConcurrentTransfers && !pendingTasks.isEmpty()) {
            TransferTask task = pendingTasks.remove(0);
            activeTasks.put(task.getId(), task);
            executeTask(task);
        }
    }
    
    /**
     * Execute transfer task
     */
    private void executeTask(TransferTask task) {
        executor.submit(() -> {
            try {
                task.setStatus(TransferTask.Status.RUNNING);
                fireEvent(TransferEvent.TASK_STARTED, task);
                
                SFTPSession.TransferProgressListener progressListener = new SFTPSession.TransferProgressListener() {
                    @Override
                    public void onStart(String src, String dest, long total) {
                        task.setTotalSize(total);
                    }
                    
                    @Override
                    public void onProgress(long transferred, long total) {
                        task.setTransferredSize(transferred);
                        task.setTotalSize(total);
                        fireEvent(TransferEvent.PROGRESS, task);
                    }
                    
                    @Override
                    public void onComplete() {
                        // Handled below
                    }
                };
                
                if (task.getType() == TransferTask.Type.DOWNLOAD) {
                    task.getSession().download(task.getSourcePath(), task.getDestPath(), progressListener);
                } else {
                    task.getSession().upload(task.getSourcePath(), task.getDestPath(), progressListener);
                }
                
                task.setStatus(TransferTask.Status.COMPLETED);
                fireEvent(TransferEvent.TASK_COMPLETED, task);
                logger.info("Transfer completed: {}", task.getSourcePath());
                
            } catch (SFTPException e) {
                task.setStatus(TransferTask.Status.FAILED);
                task.setError(e.getMessage());
                fireEvent(TransferEvent.TASK_FAILED, task);
                logger.error("Transfer failed: {}", e.getMessage());
                
            } finally {
                activeTasks.remove(task.getId());
                completedTasks.add(task);
                processQueue();
            }
        });
    }
    
    /**
     * Cancel task
     */
    public void cancelTask(String taskId) {
        TransferTask task = activeTasks.get(taskId);
        if (task != null) {
            task.setStatus(TransferTask.Status.CANCELLED);
            activeTasks.remove(taskId);
            completedTasks.add(task);
            fireEvent(TransferEvent.TASK_CANCELLED, task);
        } else {
            // Check pending
            pendingTasks.removeIf(t -> t.getId().equals(taskId));
        }
    }
    
    /**
     * Cancel all tasks
     */
    public void cancelAll() {
        pendingTasks.clear();
        for (TransferTask task : new ArrayList<>(activeTasks.values())) {
            cancelTask(task.getId());
        }
    }
    
    /**
     * Clear completed tasks
     */
    public void clearCompleted() {
        completedTasks.clear();
        fireEvent(TransferEvent.QUEUE_CLEARED, null);
    }
    
    // Listeners
    public void addListener(TransferListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(TransferListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(TransferEvent event, TransferTask task) {
        for (TransferListener listener : listeners) {
            try {
                listener.onTransferEvent(event, task);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    // Getters
    public List<TransferTask> getActiveTasks() {
        return new ArrayList<>(activeTasks.values());
    }
    
    public List<TransferTask> getPendingTasks() {
        return new ArrayList<>(pendingTasks);
    }
    
    public List<TransferTask> getCompletedTasks() {
        return new ArrayList<>(completedTasks);
    }
    
    public int getMaxConcurrentTransfers() {
        return maxConcurrentTransfers;
    }
    
    public void setMaxConcurrentTransfers(int max) {
        this.maxConcurrentTransfers = max;
    }
    
    /**
     * Shutdown manager
     */
    public void shutdown() {
        cancelAll();
        executor.shutdown();
    }
    
    /**
     * Transfer Task
     */
    public static class TransferTask {
        
        public enum Type { UPLOAD, DOWNLOAD }
        public enum Status { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED }
        
        private final String id;
        private final Type type;
        private final String sourcePath;
        private final String destPath;
        private final SFTPSession session;
        private final long startTime;
        
        private Status status = Status.PENDING;
        private long totalSize = 0;
        private long transferredSize = 0;
        private String error;
        
        public TransferTask(String id, Type type, String sourcePath, String destPath, SFTPSession session) {
            this.id = id;
            this.type = type;
            this.sourcePath = sourcePath;
            this.destPath = destPath;
            this.session = session;
            this.startTime = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public Type getType() { return type; }
        public String getSourcePath() { return sourcePath; }
        public String getDestPath() { return destPath; }
        public SFTPSession getSession() { return session; }
        public long getStartTime() { return startTime; }
        
        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        
        public long getTotalSize() { return totalSize; }
        public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
        
        public long getTransferredSize() { return transferredSize; }
        public void setTransferredSize(long transferredSize) { this.transferredSize = transferredSize; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public int getProgress() {
            if (totalSize == 0) return 0;
            return (int) (transferredSize * 100 / totalSize);
        }
        
        public String getFileName() {
            String path = type == Type.DOWNLOAD ? sourcePath : destPath;
            int sep = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
            return sep >= 0 ? path.substring(sep + 1) : path;
        }
    }
    
    /**
     * Transfer Events
     */
    public enum TransferEvent {
        TASK_ADDED,
        TASK_STARTED,
        PROGRESS,
        TASK_COMPLETED,
        TASK_FAILED,
        TASK_CANCELLED,
        QUEUE_CLEARED
    }
    
    /**
     * Transfer Listener
     */
    public interface TransferListener {
        void onTransferEvent(TransferEvent event, TransferTask task);
    }
}
