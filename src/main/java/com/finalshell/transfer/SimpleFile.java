package com.finalshell.transfer;

/**
 * 简单文件信息类
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md - SimpleFile
 */
public class SimpleFile {
    
    private String path;
    private boolean isDir;
    private long fileSize;
    private int permission;
    
    public SimpleFile(String path, long fileSize, int permission, boolean isDir) {
        this.path = path;
        this.fileSize = fileSize;
        this.permission = permission;
        this.isDir = isDir;
    }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public boolean isDir() { return isDir; }
    public void setDir(boolean dir) { isDir = dir; }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public int getPermission() { return permission; }
    public void setPermission(int permission) { this.permission = permission; }
    
    public String getName() {
        int index = path.lastIndexOf('/');
        return index >= 0 ? path.substring(index + 1) : path;
    }
    
    public String getPermissionString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isDir ? 'd' : '-');
        sb.append((permission & 0400) != 0 ? 'r' : '-');
        sb.append((permission & 0200) != 0 ? 'w' : '-');
        sb.append((permission & 0100) != 0 ? 'x' : '-');
        sb.append((permission & 040) != 0 ? 'r' : '-');
        sb.append((permission & 020) != 0 ? 'w' : '-');
        sb.append((permission & 010) != 0 ? 'x' : '-');
        sb.append((permission & 04) != 0 ? 'r' : '-');
        sb.append((permission & 02) != 0 ? 'w' : '-');
        sb.append((permission & 01) != 0 ? 'x' : '-');
        return sb.toString();
    }
}
