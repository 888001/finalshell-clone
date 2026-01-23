package com.finalshell.update;

/**
 * 下载项
 * 表示一个待下载的更新文件
 */
public class DownloadItem {
    
    private String url;
    private String fileName;
    private String targetPath;
    private long size;
    private String md5;
    private boolean downloaded;
    private long downloadedBytes;
    
    public DownloadItem() {
    }
    
    public DownloadItem(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public String getTargetPath() {
        return targetPath;
    }
    
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    public String getMd5() {
        return md5;
    }
    
    public void setMd5(String md5) {
        this.md5 = md5;
    }
    
    public boolean isDownloaded() {
        return downloaded;
    }
    
    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
    
    public long getDownloadedBytes() {
        return downloadedBytes;
    }
    
    public void setDownloadedBytes(long downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }
    
    public double getProgress() {
        if (size <= 0) return 0;
        return (double) downloadedBytes / size * 100;
    }
}
