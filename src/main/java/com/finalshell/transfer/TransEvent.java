package com.finalshell.transfer;

/**
 * 传输事件类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md - TransEvent
 */
public class TransEvent {
    
    public static final int EVENT_START = 100;
    public static final int EVENT_PROGRESS = 101;
    public static final int EVENT_COMPLETE = 102;
    public static final int EVENT_ERROR = 103;
    public static final int EVENT_CANCEL = 122;
    public static final int EVENT_PAUSE = 130;
    public static final int EVENT_RESUME = 201;
    
    // Aliases
    public static final int TYPE_START = EVENT_START;
    public static final int TYPE_PROGRESS = EVENT_PROGRESS;
    public static final int TYPE_COMPLETE = EVENT_COMPLETE;
    public static final int TYPE_ERROR = EVENT_ERROR;
    public static final int TYPE_CANCEL = EVENT_CANCEL;
    
    private int type;
    private TransTask transTask;
    private boolean download;
    
    public TransEvent(TransTask transTask, int type) {
        this.transTask = transTask;
        this.type = type;
    }
    
    public TransEvent(int type, TransTask transTask) {
        this.type = type;
        this.transTask = transTask;
    }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public TransTask getTransTask() { return transTask; }
    public void setTransTask(TransTask transTask) { this.transTask = transTask; }
    
    public boolean isDownload() { return download; }
    public void setDownload(boolean download) { this.download = download; }
    
    public boolean isStart() { return type == EVENT_START; }
    public boolean isProgress() { return type == EVENT_PROGRESS; }
    public boolean isComplete() { return type == EVENT_COMPLETE; }
    public boolean isError() { return type == EVENT_ERROR; }
    public boolean isCancelled() { return type == EVENT_CANCEL; }
}
