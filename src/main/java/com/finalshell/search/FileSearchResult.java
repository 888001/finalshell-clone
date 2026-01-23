package com.finalshell.search;

/**
 * 文件搜索结果
 */
public class FileSearchResult {
    private String path;
    private String name;
    private long size;
    private String modifyTime;
    private boolean directory;
    private String permissions;
    private String owner;
    private String matchLine;      // 匹配行内容 (grep搜索时)
    private int lineNumber;        // 匹配行号
    
    public FileSearchResult() {}
    
    public FileSearchResult(String path, String name) {
        this.path = path;
        this.name = name;
    }
    
    // Getters and Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getModifyTime() { return modifyTime; }
    public void setModifyTime(String modifyTime) { this.modifyTime = modifyTime; }
    
    public boolean isDirectory() { return directory; }
    public void setDirectory(boolean directory) { this.directory = directory; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getMatchLine() { return matchLine; }
    public void setMatchLine(String matchLine) { this.matchLine = matchLine; }
    
    public int getLineNumber() { return lineNumber; }
    public void setLineNumber(int lineNumber) { this.lineNumber = lineNumber; }
    
    public String getSizeDisplay() {
        if (directory) return "-";
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
    }
}
