package com.finalshell.sftp;

import java.io.File;

/**
 * Transfer Task - Represents a file transfer task
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class TransferTask {
    
    public enum Status {
        WAITING,
        RUNNING,
        PAUSED,
        COMPLETED,
        FAILED
    }
    
    public enum Direction {
        UPLOAD,
        DOWNLOAD
    }
    
    private String id;
    private String fileName;
    private String localPath;
    private String remotePath;
    private Direction direction;
    private Status status = Status.WAITING;
    private long totalSize;
    private long transferredSize;
    private long speed;
    private int progress;
    private String errorMessage;
    private long startTime;
    private long endTime;
    
    public TransferTask() {
        this.id = java.util.UUID.randomUUID().toString();
    }
    
    public TransferTask(String fileName, String localPath, String remotePath, Direction direction) {
        this();
        this.fileName = fileName;
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.direction = direction;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }
    
    public String getRemotePath() { return remotePath; }
    public void setRemotePath(String remotePath) { this.remotePath = remotePath; }
    
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public long getTotalSize() { return totalSize; }
    public void setTotalSize(long totalSize) { this.totalSize = totalSize; }
    
    public long getTransferredSize() { return transferredSize; }
    public void setTransferredSize(long transferredSize) { 
        this.transferredSize = transferredSize;
        if (totalSize > 0) {
            this.progress = (int) ((transferredSize * 100) / totalSize);
        }
    }
    
    public long getSpeed() { return speed; }
    public void setSpeed(long speed) { this.speed = speed; }
    
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public void start() {
        this.status = Status.RUNNING;
        this.startTime = System.currentTimeMillis();
    }
    
    public void pause() {
        this.status = Status.PAUSED;
    }
    
    public void resume() {
        this.status = Status.RUNNING;
    }
    
    public void complete() {
        this.status = Status.COMPLETED;
        this.progress = 100;
        this.endTime = System.currentTimeMillis();
    }
    
    public void fail(String errorMessage) {
        this.status = Status.FAILED;
        this.errorMessage = errorMessage;
        this.endTime = System.currentTimeMillis();
    }
    
    public boolean isRunning() {
        return status == Status.RUNNING;
    }
    
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }
    
    public boolean isFailed() {
        return status == Status.FAILED;
    }
}
