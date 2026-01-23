package com.finalshell.ui;

/**
 * 批量执行接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: MysshRoot_Classes_DeepAnalysis.md - BatchExecable
 */
public interface BatchExecable {
    
    /**
     * 在指定窗口执行操作
     * @param mainWindow 主窗口
     */
    void execute(MainWindow mainWindow);
}
