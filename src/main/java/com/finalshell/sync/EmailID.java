package com.finalshell.sync;

import java.io.Serializable;

/**
 * 邮件ID
 * 用于同步模块中唯一标识邮件/消息
 */
public class EmailID implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String folder;
    private long uid;
    private long timestamp;
    
    public EmailID() {}
    
    public EmailID(String id) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
    }
    
    public EmailID(String folder, long uid) {
        this.folder = folder;
        this.uid = uid;
        this.id = folder + "_" + uid;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getFolder() { return folder; }
    public void setFolder(String folder) { this.folder = folder; }
    
    public long getUid() { return uid; }
    public void setUid(long uid) { this.uid = uid; }
    
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EmailID other = (EmailID) obj;
        return id != null ? id.equals(other.id) : other.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "EmailID{id='" + id + "', folder='" + folder + "', uid=" + uid + "}";
    }
}
