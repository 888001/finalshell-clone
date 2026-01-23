package com.finalshell.monitor;

/**
 * 磁盘信息
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class DiskInfo {
    
    private String fileSystem;
    private long totalSize;
    private long usedSize;
    private long availableSize;
    private String mountPoint;
    
    public DiskInfo() {
    }
    
    public DiskInfo(String fileSystem, long totalSize, long usedSize, long availableSize, String mountPoint) {
        this.fileSystem = fileSystem;
        this.totalSize = totalSize;
        this.usedSize = usedSize;
        this.availableSize = availableSize;
        this.mountPoint = mountPoint;
    }
    
    public String getFileSystem() {
        return fileSystem;
    }
    
    public void setFileSystem(String fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public long getTotalSize() {
        return totalSize;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    
    public long getUsedSize() {
        return usedSize;
    }
    
    public void setUsedSize(long usedSize) {
        this.usedSize = usedSize;
    }
    
    public long getAvailableSize() {
        return availableSize;
    }
    
    public void setAvailableSize(long availableSize) {
        this.availableSize = availableSize;
    }
    
    public String getMountPoint() {
        return mountPoint;
    }
    
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }
    
    public double getUsagePercent() {
        if (totalSize <= 0) return 0;
        return (usedSize * 100.0) / totalSize;
    }
}
