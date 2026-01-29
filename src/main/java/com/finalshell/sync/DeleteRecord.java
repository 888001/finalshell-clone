package com.finalshell.sync;

/**
 * 删除记录
 * 记录同步过程中被删除的项目信息
 */
public class DeleteRecord {
    
    private String id;
    private String type;
    private String name;
    private long deleteTime;
    private String deletedBy;
    private String extra;
    
    public DeleteRecord() {
        this.deleteTime = System.currentTimeMillis();
    }
    
    public DeleteRecord(String id, String type) {
        this();
        this.id = id;
        this.type = type;
    }
    
    public DeleteRecord(String id, String type, String name) {
        this(id, type);
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getDeleteTime() {
        return deleteTime;
    }
    
    public void setDeleteTime(long deleteTime) {
        this.deleteTime = deleteTime;
    }
    
    public String getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    public String getExtra() {
        return extra;
    }
    
    public void setExtra(String extra) {
        this.extra = extra;
    }
    
    // Alias methods for compatibility
    public String getPath() { return id; }
    public void setPath(String path) { this.id = path; }
    
    public long getTimestamp() { return deleteTime; }
    public void setTimestamp(long timestamp) { this.deleteTime = timestamp; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteRecord that = (DeleteRecord) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "DeleteRecord{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", deleteTime=" + deleteTime +
                '}';
    }
}
