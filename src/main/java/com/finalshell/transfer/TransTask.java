package com.finalshell.transfer;

import java.io.File;

/**
 * 传输任务
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md - TransTask
 */
public class TransTask {
    
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_PAUSED = 2;
    public static final int STATUS_COMPLETE = 3;
    public static final int STATUS_ERROR = 4;
    public static final int STATUS_CANCELLED = 5;
    
    private String id;
    private String localPath;
    private String remotePath;
    private boolean download;
    private long totalSize;
    private long transferredSize;
    private int status = STATUS_WAITING;
    private String errorMessage;
    private long startTime;
    private long endTime;
    
    public TransTask(String localPath, String remotePath, boolean download) {
        this.id = System.currentTimeMillis() + "-" + Math.random();
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.download = download;
    }
    
    public void start() {
        this.status = STATUS_RUNNING;
        this.startTime = System.currentTimeMillis();
    }
    
    public void pause() {
        this.status = STATUS_PAUSED;
    }
    
    public void resume() {
        this.status = STATUS_RUNNING;
    }
    
    public void complete() {
        this.status = STATUS_COMPLETE;
        this.endTime = System.currentTimeMillis();
    }
    
    public void error(String message) {
        this.status = STATUS_ERROR;
        this.errorMessage = message;
        this.endTime = System.currentTimeMillis();
    }
    
    public void cancel() {
        this.status = STATUS_CANCELLED;
        this.endTime = System.currentTimeMillis();
    }
    
    public double getProgress() {
        if (totalSize == 0) return 0;
        return (double) transferredSize / totalSize * 100;
    }
    
    public long getSpeed() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed <= 0) return 0;
        return transferredSize * 1000 / elapsed;
    }
    
    public String getFileName() {
        String path = download ? remotePath : localPath;
        int index = path.lastIndexOf('/');
        if (index < 0) index = path.lastIndexOf('\\');
        return index >= 0 ? path.substring(index + 1) : path;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getLocalPath() { return localPath; }
    public String getRemotePath() { return remotePath; }
    public boolean isDownload() { return download; }
    
    public long getTotalSize() { return totalSize; }
    public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
    
    public long getTransferredSize() { return transferredSize; }
    public void setTransferredSize(long transferredSize) { this.transferredSize = transferredSize; }
    
    public int getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
}
