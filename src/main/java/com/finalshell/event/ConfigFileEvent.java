package com.finalshell.event;

import java.io.File;
import java.util.EventObject;

/**
 * 配置文件事件
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class ConfigFileEvent extends EventObject {
    
    public static final int TYPE_CREATED = 1;
    public static final int TYPE_MODIFIED = 2;
    public static final int TYPE_DELETED = 3;
    public static final int TYPE_RENAMED = 4;
    
    private int type;
    private File file;
    private File oldFile;
    private long timestamp;
    
    public ConfigFileEvent(Object source, int type, File file) {
        super(source);
        this.type = type;
        this.file = file;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ConfigFileEvent(Object source, int type, File oldFile, File newFile) {
        super(source);
        this.type = type;
        this.oldFile = oldFile;
        this.file = newFile;
        this.timestamp = System.currentTimeMillis();
    }
    
    public int getType() {
        return type;
    }
    
    public File getFile() {
        return file;
    }
    
    public File getOldFile() {
        return oldFile;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public String getTypeString() {
        switch (type) {
            case TYPE_CREATED: return "创建";
            case TYPE_MODIFIED: return "修改";
            case TYPE_DELETED: return "删除";
            case TYPE_RENAMED: return "重命名";
            default: return "未知";
        }
    }
    
    public boolean isCreated() {
        return type == TYPE_CREATED;
    }
    
    public boolean isModified() {
        return type == TYPE_MODIFIED;
    }
    
    public boolean isDeleted() {
        return type == TYPE_DELETED;
    }
    
    public boolean isRenamed() {
        return type == TYPE_RENAMED;
    }
}
