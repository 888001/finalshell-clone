package com.finalshell.ui;

import java.io.*;
import java.sql.Timestamp;

/**
 * 虚拟文件
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Core_UI_Components_DeepAnalysis.md - VFile
 */
public class VFile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private long fileId;
    private long parentDirId;
    private String md5;
    private long length;
    private String name;
    private Timestamp createTime;
    private File file;
    private int type;
    
    public static final int TYPE_FILE = 1;
    public static final int TYPE_DIRECTORY = 2;
    public static final int TYPE_LINK = 3;
    
    public VFile() {}
    
    public VFile(File file) {
        this.file = file;
        this.name = file.getName();
        this.length = file.length();
        this.type = file.isDirectory() ? TYPE_DIRECTORY : TYPE_FILE;
        this.createTime = new Timestamp(file.lastModified());
    }
    
    public VFile(String name, long length, int type) {
        this.name = name;
        this.length = length;
        this.type = type;
    }
    
    public boolean isDirectory() {
        return type == TYPE_DIRECTORY;
    }
    
    public boolean isFile() {
        return type == TYPE_FILE;
    }
    
    public boolean isLink() {
        return type == TYPE_LINK;
    }
    
    // Getters and Setters
    public long getFileId() { return fileId; }
    public void setFileId(long fileId) { this.fileId = fileId; }
    
    public long getParentDirId() { return parentDirId; }
    public void setParentDirId(long parentDirId) { this.parentDirId = parentDirId; }
    
    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }
    
    public long getLength() { return length; }
    public void setLength(long length) { this.length = length; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Timestamp getCreateTime() { return createTime; }
    public void setCreateTime(Timestamp createTime) { this.createTime = createTime; }
    
    public File getFile() { return file; }
    public void setFile(File file) { this.file = file; }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    @Override
    public String toString() {
        return name;
    }
}
