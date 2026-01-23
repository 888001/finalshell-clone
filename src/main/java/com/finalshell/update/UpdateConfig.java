package com.finalshell.update;

import java.util.ArrayList;
import java.util.List;

/**
 * 更新配置
 * 存储更新服务器和版本信息
 */
public class UpdateConfig {
    
    private String version;
    private String updateUrl;
    private String releaseNotes;
    private long releaseDate;
    private boolean forceUpdate;
    private List<DownloadItem> downloadItems;
    
    public UpdateConfig() {
        this.downloadItems = new ArrayList<>();
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getUpdateUrl() {
        return updateUrl;
    }
    
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }
    
    public String getReleaseNotes() {
        return releaseNotes;
    }
    
    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }
    
    public long getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(long releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public boolean isForceUpdate() {
        return forceUpdate;
    }
    
    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }
    
    public List<DownloadItem> getDownloadItems() {
        return downloadItems;
    }
    
    public void setDownloadItems(List<DownloadItem> downloadItems) {
        this.downloadItems = downloadItems;
    }
    
    public void addDownloadItem(DownloadItem item) {
        downloadItems.add(item);
    }
    
    public long getTotalSize() {
        long total = 0;
        for (DownloadItem item : downloadItems) {
            total += item.getSize();
        }
        return total;
    }
}
