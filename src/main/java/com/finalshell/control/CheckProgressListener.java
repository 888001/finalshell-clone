package com.finalshell.control;

/**
 * 检查进度监听器接口
 * 用于监听检查任务的进度
 */
public interface CheckProgressListener {
    
    /**
     * 进度更新回调
     */
    void onProgressUpdate();
}
