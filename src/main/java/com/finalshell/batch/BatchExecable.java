package com.finalshell.batch;

/**
 * 批量执行接口
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public interface BatchExecable {
    
    void execute(String command);
    
    void executeAll(String[] commands);
    
    void cancel();
    
    boolean isRunning();
    
    String getOutput();
    
    int getExitCode();
}
