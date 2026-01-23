package com.finalshell.ssh;

import java.io.Serializable;

/**
 * SSH文件对象封装
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSH_Terminal_Analysis.md - SSHFile
 */
public class SSHFile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final int TYPE_FILE = 1;
    public static final int TYPE_DIR = 2;
    public static final int TYPE_LINK = 3;
    public static final int TYPE_SOCKET = 4;
    public static final int TYPE_CHAR = 5;
    public static final int TYPE_BLOCK = 6;
    
    private String name;
    private String path;
    private String attr;
    private int type;
    private long size;
    private long mtime;
    private boolean isDir;
    private boolean isLink;
    private String linkDst;
    private String owner;
    private String group;
    private int permissions;
    
    public SSHFile() {}
    
    public SSHFile(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    public void parseAttr(String attr) {
        this.attr = attr;
        if (attr != null && attr.length() > 0) {
            char first = attr.charAt(0);
            switch (first) {
                case 'd': 
                    this.type = TYPE_DIR;
                    this.isDir = true;
                    break;
                case 'l':
                    this.type = TYPE_LINK;
                    this.isLink = true;
                    break;
                case 's':
                    this.type = TYPE_SOCKET;
                    break;
                case 'c':
                    this.type = TYPE_CHAR;
                    break;
                case 'b':
                    this.type = TYPE_BLOCK;
                    break;
                default:
                    this.type = TYPE_FILE;
                    break;
            }
        }
    }
    
    public String getFullPath() {
        if (path.endsWith("/")) {
            return path + name;
        }
        return path + "/" + name;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getAttr() { return attr; }
    public void setAttr(String attr) { this.attr = attr; }
    
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public long getMtime() { return mtime; }
    public void setMtime(long mtime) { this.mtime = mtime; }
    
    public boolean isDir() { return isDir; }
    public void setDir(boolean isDir) { this.isDir = isDir; }
    
    public boolean isLink() { return isLink; }
    public void setLink(boolean isLink) { this.isLink = isLink; }
    
    public String getLinkDst() { return linkDst; }
    public void setLinkDst(String linkDst) { this.linkDst = linkDst; }
    
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    
    public int getPermissions() { return permissions; }
    public void setPermissions(int permissions) { this.permissions = permissions; }
    
    @Override
    public String toString() {
        return name;
    }
}
