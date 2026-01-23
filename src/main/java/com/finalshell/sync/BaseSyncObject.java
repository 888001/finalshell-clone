package com.finalshell.sync;

import java.io.Serializable;

/**
 * 同步对象基类
 * 所有需要同步的对象的基类
 */
public abstract class BaseSyncObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String name;
    private long createTime;
    private long modifyTime;
    private long syncTime;
    private int version;
    private boolean deleted;
    private String checksum;
    
    public BaseSyncObject() {
        this.createTime = System.currentTimeMillis();
        this.modifyTime = this.createTime;
        this.version = 1;
        this.deleted = false;
    }
    
    public BaseSyncObject(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getModifyTime() {
        return modifyTime;
    }
    
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    public long getSyncTime() {
        return syncTime;
    }
    
    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public void incrementVersion() {
        this.version++;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public boolean isDeleted() {
        return deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        this.modifyTime = System.currentTimeMillis();
    }
    
    public String getChecksum() {
        return checksum;
    }
    
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }
    
    public abstract String toJson();
    
    public abstract void fromJson(String json);
    
    public boolean needsSync() {
        return syncTime < modifyTime;
    }
    
    public void markSynced() {
        this.syncTime = System.currentTimeMillis();
    }
}
