package com.finalshell.ftp;

/**
 * FTP File Entry
 */
public class FTPFile {
    
    private String name;
    private long size;
    private String date;
    private String permissions;
    private boolean directory;
    private boolean link;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }
    
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    
    public boolean isDirectory() { return directory; }
    public void setDirectory(boolean directory) { this.directory = directory; }
    
    public boolean isLink() { return link; }
    public void setLink(boolean link) { this.link = link; }
    
    @Override
    public String toString() {
        return (directory ? "[DIR] " : "") + name + " (" + size + " bytes)";
    }
}
