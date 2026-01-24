package com.finalshell.sync;

/**
 * Sync Configuration - Settings for data synchronization
 * 
 * Based on analysis of FinalShell 3.8.3
 * Replaces cloud sync with local/WebDAV alternatives
 */
public class SyncConfig {
    
    public enum SyncType {
        LOCAL,      // Local file export/import
        WEBDAV,     // WebDAV server sync
        SFTP        // SFTP server sync
    }
    
    private SyncType type = SyncType.LOCAL;
    
    // Local sync settings
    private String localPath;
    
    // WebDAV settings
    private String webdavUrl;
    private String webdavUsername;
    private String webdavPassword;
    private String webdavPath = "/finalshell-backup";
    
    // SFTP sync settings
    private String sftpConnectionId;
    private String sftpRemotePath = "/home/backup/finalshell";
    
    // General settings
    private boolean enabled = false;
    private String email;
    private boolean autoSync = false;
    private int syncIntervalMinutes = 30;
    private boolean encryptBackup = true;
    private String encryptionPassword;
    
    // What to sync
    private boolean syncConnections = true;
    private boolean syncQuickCommands = true;
    private boolean syncSettings = true;
    private boolean syncHistory = false;
    
    public SyncConfig() {}
    
    // Getters and Setters
    public SyncType getType() { return type; }
    public void setType(SyncType type) { this.type = type; }
    
    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }
    
    public String getWebdavUrl() { return webdavUrl; }
    public void setWebdavUrl(String webdavUrl) { this.webdavUrl = webdavUrl; }
    
    public String getWebdavUsername() { return webdavUsername; }
    public void setWebdavUsername(String webdavUsername) { this.webdavUsername = webdavUsername; }
    
    public String getWebdavPassword() { return webdavPassword; }
    public void setWebdavPassword(String webdavPassword) { this.webdavPassword = webdavPassword; }
    
    public String getWebdavPath() { return webdavPath; }
    public void setWebdavPath(String webdavPath) { this.webdavPath = webdavPath; }
    
    public String getSftpConnectionId() { return sftpConnectionId; }
    public void setSftpConnectionId(String sftpConnectionId) { this.sftpConnectionId = sftpConnectionId; }
    
    public String getSftpRemotePath() { return sftpRemotePath; }
    public void setSftpRemotePath(String sftpRemotePath) { this.sftpRemotePath = sftpRemotePath; }
    
    public boolean isAutoSync() { return autoSync; }
    public void setAutoSync(boolean autoSync) { this.autoSync = autoSync; }
    
    public int getSyncIntervalMinutes() { return syncIntervalMinutes; }
    public void setSyncIntervalMinutes(int syncIntervalMinutes) { this.syncIntervalMinutes = syncIntervalMinutes; }
    
    public boolean isEncryptBackup() { return encryptBackup; }
    public void setEncryptBackup(boolean encryptBackup) { this.encryptBackup = encryptBackup; }
    
    public String getEncryptionPassword() { return encryptionPassword; }
    public void setEncryptionPassword(String encryptionPassword) { this.encryptionPassword = encryptionPassword; }
    
    public boolean isSyncConnections() { return syncConnections; }
    public void setSyncConnections(boolean syncConnections) { this.syncConnections = syncConnections; }
    
    public boolean isSyncQuickCommands() { return syncQuickCommands; }
    public void setSyncQuickCommands(boolean syncQuickCommands) { this.syncQuickCommands = syncQuickCommands; }
    
    public boolean isSyncSettings() { return syncSettings; }
    public void setSyncSettings(boolean syncSettings) { this.syncSettings = syncSettings; }
    
    public boolean isSyncHistory() { return syncHistory; }
    public void setSyncHistory(boolean syncHistory) { this.syncHistory = syncHistory; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // Compatibility methods for SyncClient
    public String getUsername() { return webdavUsername; }
    public String getPassword() { return webdavPassword; }
    public String getSyncServerUrl() { return webdavUrl; }
    
    /**
     * Validate configuration
     */
    public boolean isValid() {
        switch (type) {
            case LOCAL:
                return localPath != null && !localPath.isEmpty();
            case WEBDAV:
                return webdavUrl != null && !webdavUrl.isEmpty();
            case SFTP:
                return sftpConnectionId != null && !sftpConnectionId.isEmpty();
            default:
                return false;
        }
    }
    
    /**
     * Get display description
     */
    public String getDescription() {
        switch (type) {
            case LOCAL:
                return "本地: " + (localPath != null ? localPath : "未设置");
            case WEBDAV:
                return "WebDAV: " + (webdavUrl != null ? webdavUrl : "未设置");
            case SFTP:
                return "SFTP: " + (sftpRemotePath != null ? sftpRemotePath : "未设置");
            default:
                return "未知";
        }
    }
}
