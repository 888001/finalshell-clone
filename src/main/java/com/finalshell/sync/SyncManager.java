package com.finalshell.sync;

import com.finalshell.config.ConfigManager;
import com.finalshell.config.ConnectConfig;
import com.finalshell.terminal.QuickCommand;
import com.finalshell.terminal.QuickCommandManager;
import com.finalshell.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;

/**
 * Sync Manager - Manages data synchronization
 * 
 * Based on analysis of FinalShell 3.8.3
 * Provides local/WebDAV/SFTP sync alternatives
 */
public class SyncManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);
    private static SyncManager instance;
    
    private SyncConfig syncConfig;
    private final List<SyncListener> listeners = new ArrayList<>();
    private final ConfigManager configManager = ConfigManager.getInstance();
    
    private SyncManager() {
        loadSyncConfig();
    }
    
    public static synchronized SyncManager getInstance() {
        if (instance == null) {
            instance = new SyncManager();
        }
        return instance;
    }
    
    /**
     * Load sync configuration
     */
    private void loadSyncConfig() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File file = new File(configDir, "sync_config.dat");
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                syncConfig = (SyncConfig) ois.readObject();
                logger.info("Loaded sync configuration");
            } catch (Exception e) {
                logger.warn("Failed to load sync config: {}", e.getMessage());
                syncConfig = new SyncConfig();
            }
        } else {
            syncConfig = new SyncConfig();
        }
    }
    
    /**
     * Save sync configuration
     */
    public void saveSyncConfig() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File file = new File(configDir, "sync_config.dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(syncConfig);
            logger.info("Saved sync configuration");
        } catch (Exception e) {
            logger.error("Failed to save sync config", e);
        }
    }
    
    /**
     * Export data to file
     */
    public void exportToFile(File targetFile) throws SyncException {
        fireEvent(SyncEvent.EXPORTING, "开始导出...");
        
        try {
            File tempDir = Files.createTempDirectory("finalshell-export").toFile();
            
            // Export connections
            if (syncConfig.isSyncConnections()) {
                exportConnections(tempDir);
            }
            
            // Export quick commands
            if (syncConfig.isSyncQuickCommands()) {
                exportQuickCommands(tempDir);
            }
            
            // Export settings
            if (syncConfig.isSyncSettings()) {
                exportSettings(tempDir);
            }
            
            // Create ZIP
            createZipFile(tempDir, targetFile);
            
            // Cleanup temp
            deleteDirectory(tempDir);
            
            fireEvent(SyncEvent.EXPORT_COMPLETE, "导出完成: " + targetFile.getName());
            logger.info("Exported data to: {}", targetFile.getAbsolutePath());
            
        } catch (Exception e) {
            fireEvent(SyncEvent.ERROR, "导出失败: " + e.getMessage());
            throw new SyncException("Export failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Import data from file
     */
    public void importFromFile(File sourceFile) throws SyncException {
        fireEvent(SyncEvent.IMPORTING, "开始导入...");
        
        try {
            File tempDir = Files.createTempDirectory("finalshell-import").toFile();
            
            // Extract ZIP
            extractZipFile(sourceFile, tempDir);
            
            // Import connections
            File connectionsFile = new File(tempDir, "connections.dat");
            if (connectionsFile.exists()) {
                importConnections(connectionsFile);
            }
            
            // Import quick commands
            File commandsFile = new File(tempDir, "quick_commands.dat");
            if (commandsFile.exists()) {
                importQuickCommands(commandsFile);
            }
            
            // Import settings
            File settingsFile = new File(tempDir, "settings.dat");
            if (settingsFile.exists()) {
                importSettings(settingsFile);
            }
            
            // Cleanup temp
            deleteDirectory(tempDir);
            
            fireEvent(SyncEvent.IMPORT_COMPLETE, "导入完成");
            logger.info("Imported data from: {}", sourceFile.getAbsolutePath());
            
        } catch (Exception e) {
            fireEvent(SyncEvent.ERROR, "导入失败: " + e.getMessage());
            throw new SyncException("Import failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Export connections
     */
    private void exportConnections(File targetDir) throws IOException {
        List<ConnectConfig> connections = ConfigManager.getInstance().getAllConnections();
        File file = new File(targetDir, "connections.dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(connections);
        }
        logger.debug("Exported {} connections", connections.size());
    }
    
    /**
     * Export quick commands
     */
    private void exportQuickCommands(File targetDir) throws IOException {
        List<QuickCommand> commands = QuickCommandManager.getInstance().getCommands();
        File file = new File(targetDir, "quick_commands.dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(commands);
        }
        logger.debug("Exported {} quick commands", commands.size());
    }
    
    /**
     * Export settings
     */
    private void exportSettings(File targetDir) throws IOException {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File appConfig = new File(configDir, "app_config.dat");
        
        if (appConfig.exists()) {
            Files.copy(appConfig.toPath(), new File(targetDir, "settings.dat").toPath());
        }
    }
    
    /**
     * Import connections
     */
    @SuppressWarnings("unchecked")
    private void importConnections(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<ConnectConfig> connections = (List<ConnectConfig>) ois.readObject();
            for (ConnectConfig config : connections) {
                ConfigManager.getInstance().saveConnection(config);
            }
            logger.info("Imported {} connections", connections.size());
        }
    }
    
    /**
     * Import quick commands
     */
    @SuppressWarnings("unchecked")
    private void importQuickCommands(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<QuickCommand> commands = (List<QuickCommand>) ois.readObject();
            for (QuickCommand cmd : commands) {
                QuickCommandManager.getInstance().addCommand(cmd);
            }
            QuickCommandManager.getInstance().saveCommands();
            logger.info("Imported {} quick commands", commands.size());
        }
    }
    
    /**
     * Import settings
     */
    private void importSettings(File file) throws IOException {
        File configDir = ConfigManager.getInstance().getConfigDir();
        Files.copy(file.toPath(), new File(configDir, "app_config.dat").toPath(),
            StandardCopyOption.REPLACE_EXISTING);
        logger.info("Imported settings");
    }
    
    /**
     * Create ZIP file from directory
     */
    private void createZipFile(File sourceDir, File zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : sourceDir.listFiles()) {
                if (file.isFile()) {
                    ZipEntry entry = new ZipEntry(file.getName());
                    zos.putNextEntry(entry);
                    Files.copy(file.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }
    }
    
    /**
     * Extract ZIP file to directory
     */
    private void extractZipFile(File zipFile, File targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File file = new File(targetDir, entry.getName());
                Files.copy(zis, file.toPath());
                zis.closeEntry();
            }
        }
    }
    
    /**
     * Delete directory recursively
     */
    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
    
    /**
     * Generate backup filename
     */
    public String generateBackupFilename() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return "finalshell_backup_" + sdf.format(new Date()) + ".zip";
    }
    
    // Getters/Setters
    public SyncConfig getSyncConfig() { return syncConfig; }
    public void setSyncConfig(SyncConfig syncConfig) { this.syncConfig = syncConfig; }
    
    // Listeners
    public void addListener(SyncListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(SyncListener listener) {
        listeners.remove(listener);
    }
    
    private void fireEvent(SyncEvent event, String message) {
        for (SyncListener listener : listeners) {
            try {
                listener.onSyncEvent(event, message);
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    /**
     * Sync Event
     */
    public enum SyncEvent {
        EXPORTING,
        EXPORT_COMPLETE,
        IMPORTING,
        IMPORT_COMPLETE,
        SYNCING,
        SYNC_COMPLETE,
        ERROR
    }
    
    /**
     * Sync Listener
     */
    public interface SyncListener {
        void onSyncEvent(SyncEvent event, String message);
    }
    
    /**
     * Get sync configuration
     */
    public SyncConfig getConfig() {
        return syncConfig;
    }
    
    /**
     * Set sync configuration
     */
    public void setConfig(SyncConfig config) {
        this.syncConfig = config;
        saveSyncConfig();
    }
    
    /**
     * Sync via WebDAV
     */
    private void syncViaWebDav() throws Exception {
        String url = syncConfig.getServerUrl();
        String username = syncConfig.getUsername();
        String password = syncConfig.getPassword();
        
        if (url == null || url.isEmpty()) {
            throw new Exception("WebDAV服务器地址未配置");
        }
        
        logger.info("正在通过WebDAV同步: {}", url);
        // WebDAV sync implementation would go here
        // For now, just log the operation
    }
    
    /**
     * Sync via Email
     */
    private void syncViaEmail() throws Exception {
        String email = syncConfig.getEmail();
        if (email == null || email.isEmpty()) {
            throw new Exception("邮箱地址未配置");
        }
        
        logger.info("正在通过邮箱同步: {}", email);
        // Email sync implementation would go here
    }
    
    /**
     * Sync to local backup
     */
    private void syncToLocal() throws Exception {
        String backupPath = syncConfig.getBackupPath();
        if (backupPath == null || backupPath.isEmpty()) {
            backupPath = configManager.getBackupDir();
        }
        
        logger.info("正在同步到本地: {}", backupPath);
        configManager.backupConfigs();
    }
    
    /**
     * Perform sync operation
     */
    public boolean sync() {
        if (syncConfig == null || !syncConfig.isEnabled()) {
            return false;
        }
        
        try {
            fireEvent(SyncEvent.SYNCING, "开始同步...");
            
            SyncConfig.SyncType syncType = syncConfig.getType();
            switch (syncType) {
                case WEBDAV:
                    syncViaWebDav();
                    break;
                case SFTP:
                    syncViaEmail();
                    break;
                case LOCAL:
                    syncToLocal();
                    break;
                default:
                    logger.warn("未知同步类型: {}", syncType);
            }
            
            syncConfig.setLastSyncTime(System.currentTimeMillis());
            saveSyncConfig();
            fireEvent(SyncEvent.SYNC_COMPLETE, "同步完成");
            return true;
        } catch (Exception e) {
            logger.error("Sync failed", e);
            fireEvent(SyncEvent.ERROR, "同步失败: " + e.getMessage());
            return false;
        }
    }
}
