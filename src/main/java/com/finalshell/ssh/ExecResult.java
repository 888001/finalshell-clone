package com.finalshell.ssh;

/**
 * 命令执行结果
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - ExecResult
 */
public class ExecResult {
    
    private String stdout;
    private String stderr;
    private int exitCode;
    private boolean success;
    private long duration;
    
    public ExecResult() {}
    
    public ExecResult(int exitCode, String stdout, String stderr) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
        this.success = (exitCode == 0);
    }
    
    public boolean hasError() {
        return stderr != null && !stderr.isEmpty();
    }
    
    // Getters and Setters
    public String getStdout() { return stdout; }
    public void setStdout(String stdout) { this.stdout = stdout; }
    
    public String getStderr() { return stderr; }
    public void setStderr(String stderr) { this.stderr = stderr; }
    
    public int getExitCode() { return exitCode; }
    public void setExitCode(int exitCode) { this.exitCode = exitCode; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
}
