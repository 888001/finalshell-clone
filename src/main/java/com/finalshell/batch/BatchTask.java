package com.finalshell.batch;

import com.finalshell.config.ConnectConfig;

/**
 * 批量执行任务
 */
public class BatchTask {
    private String id;
    private ConnectConfig connection;
    private String command;
    private BatchTaskStatus status;
    private String output;
    private String error;
    private long startTime;
    private long endTime;
    private int exitCode;
    
    public enum BatchTaskStatus {
        PENDING("等待中"),
        RUNNING("执行中"),
        SUCCESS("成功"),
        FAILED("失败"),
        CANCELLED("已取消"),
        TIMEOUT("超时");
        
        private final String displayName;
        
        BatchTaskStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public BatchTask(ConnectConfig connection, String command) {
        this.id = java.util.UUID.randomUUID().toString();
        this.connection = connection;
        this.command = command;
        this.status = BatchTaskStatus.PENDING;
        this.output = "";
        this.error = "";
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public ConnectConfig getConnection() { return connection; }
    public void setConnection(ConnectConfig connection) { this.connection = connection; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public BatchTaskStatus getStatus() { return status; }
    public void setStatus(BatchTaskStatus status) { this.status = status; }
    
    public String getOutput() { return output; }
    public void setOutput(String output) { this.output = output; }
    
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    
    public long getDuration() {
        if (startTime == 0) return 0;
        if (endTime == 0) return System.currentTimeMillis() - startTime;
        return endTime - startTime;
    }
    
    public String getHostDisplay() {
        return connection.getUsername() + "@" + connection.getHost();
    }
}
