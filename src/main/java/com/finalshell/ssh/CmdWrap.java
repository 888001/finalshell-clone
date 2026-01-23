package com.finalshell.ssh;

/**
 * 命令包装器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - CmdWrap
 */
public class CmdWrap {
    
    private String command;
    private String result;
    private int exitCode;
    private long startTime;
    private long endTime;
    private boolean success;
    private Throwable exception;
    
    public CmdWrap() {}
    
    public CmdWrap(String command) {
        this.command = command;
    }
    
    public long getDuration() {
        return endTime - startTime;
    }
    
    // Getters and Setters
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
    
    public long getEndTime() { return endTime; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Throwable getException() { return exception; }
    public void setException(Throwable exception) { this.exception = exception; }
}
