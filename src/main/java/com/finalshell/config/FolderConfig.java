package com.finalshell.config;

/**
 * Folder Configuration for organizing connections
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DataModel_ConfigFormat.md - FolderConfig
 */
public class FolderConfig {
    
    private String id;
    private String name;
    private String description;
    private String parentId;
    private int sortOrder = 0;
    private boolean expanded = true;
    private long createTime;
    private long updateTime;
    
    public FolderConfig() {
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }
    
    public FolderConfig(String name) {
        this();
        this.name = name;
    }
    
    // Getters and Setters
    
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
        this.updateTime = System.currentTimeMillis();
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    public int getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
    
    public long getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
    
    public long getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return name;
    }
}
