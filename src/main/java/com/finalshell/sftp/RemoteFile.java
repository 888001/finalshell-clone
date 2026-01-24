package com.finalshell.sftp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Remote File - Represents a file/directory on remote server
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class RemoteFile {
    
    private String name;
    private String path;
    private boolean directory;
    private boolean link;
    private long size;
    private String permissions;
    private long modifyTime;
    private int uid;
    private int gid;
    
    public RemoteFile() {
    }
    
    public RemoteFile(String name, String path, boolean directory) {
        this.name = name;
        this.path = path;
        this.directory = directory;
    }
    
    // Getters and Setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public boolean isDirectory() {
        return directory;
    }
    
    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
    
    public boolean isLink() {
        return link;
    }
    
    public void setLink(boolean link) {
        this.link = link;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public String getPermissions() {
        return permissions;
    }
    
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
    
    public long getModifyTime() {
        return modifyTime;
    }
    
    public long getModifiedTime() {
        return modifyTime;
    }
    
    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    // Alias for setModifyTime
    public void setMtime(long mtime) {
        this.modifyTime = mtime;
    }
    
    public String getFullPath() {
        if (path == null) return name;
        if (path.endsWith("/")) return path + name;
        return path + "/" + name;
    }
    
    public int getUid() {
        return uid;
    }
    
    public void setUid(int uid) {
        this.uid = uid;
    }
    
    public int getGid() {
        return gid;
    }
    
    public void setGid(int gid) {
        this.gid = gid;
    }
    
    /**
     * Get formatted size string
     */
    public String getFormattedSize() {
        if (directory) {
            return "";
        }
        
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
        }
    }
    
    /**
     * Get formatted modify time
     */
    public String getFormattedTime() {
        if (modifyTime == 0) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(modifyTime));
    }
    
    /**
     * Get file extension
     */
    public String getExtension() {
        if (directory || name == null) {
            return "";
        }
        int dot = name.lastIndexOf('.');
        if (dot > 0 && dot < name.length() - 1) {
            return name.substring(dot + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * Check if file is hidden
     */
    public boolean isHidden() {
        return name != null && name.startsWith(".");
    }
    
    @Override
    public String toString() {
        return name;
    }
}
