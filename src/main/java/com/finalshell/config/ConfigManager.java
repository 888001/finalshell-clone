package com.finalshell.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.finalshell.util.EncryptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Configuration Manager - Handles all configuration read/write operations
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: DataModel_ConfigFormat.md
 * 
 * Configuration directory structure:
 * Windows:  %APPDATA%/finalshell/
 * Linux:    ~/.finalshell/
 * macOS:    ~/Library/Application Support/finalshell/
 */
public class ConfigManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    
    private static ConfigManager instance;
    
    private final Path configDir;
    private final Path connectDir;
    private final Path backupDir;
    
    private AppConfig appConfig;
    private Map<String, ConnectConfig> connections = new HashMap<>();
    private Map<String, ConnectConfig> connectionsByPath = new HashMap<>();
    private Map<String, FolderConfig> foldersById = new HashMap<>();
    private List<FolderConfig> folders = new ArrayList<>();
    
    // Event listeners
    private List<ConfigChangeListener> listeners = new ArrayList<>();
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }
    
    private ConfigManager() {
        this.configDir = getConfigDirectory();
        this.connectDir = configDir.resolve("conn");
        this.backupDir = configDir.resolve("backup");
        
        initDirectories();
        loadAllConfigs();
    }
    
    private Path getConfigDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        Path baseDir;
        
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            baseDir = Paths.get(appData, "finalshell");
        } else if (os.contains("mac")) {
            String home = System.getProperty("user.home");
            baseDir = Paths.get(home, "Library", "Application Support", "finalshell");
        } else {
            // Linux and other Unix-like systems
            String home = System.getProperty("user.home");
            baseDir = Paths.get(home, ".finalshell");
        }
        
        return baseDir;
    }
    
    private void initDirectories() {
        try {
            Files.createDirectories(configDir);
            Files.createDirectories(connectDir);
            Files.createDirectories(backupDir);
            logger.info("Configuration directories initialized: {}", configDir);
        } catch (IOException e) {
            logger.error("Failed to create config directories", e);
        }
    }
    
    private void loadAllConfigs() {
        loadAppConfig();
        loadConnections();
        loadFolders();
    }
    
    private void loadAppConfig() {
        Path configFile = configDir.resolve("config.json");
        
        if (Files.exists(configFile)) {
            try {
                String json = new String(Files.readAllBytes(configFile), StandardCharsets.UTF_8);
                appConfig = JSON.parseObject(json, AppConfig.class);
                logger.info("App config loaded");
            } catch (Exception e) {
                logger.error("Failed to load app config", e);
                appConfig = new AppConfig();
            }
        } else {
            appConfig = new AppConfig();
            saveAppConfig();
        }
    }
    
    private void loadConnections() {
        if (!Files.exists(connectDir)) {
            return;
        }
        
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(connectDir, "*.json")) {
            for (Path file : stream) {
                try {
                    String json = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                    ConnectConfig config = JSON.parseObject(json, ConnectConfig.class);
                    if (config != null && config.getId() != null) {
                        connections.put(config.getId(), config);
                    }
                } catch (Exception e) {
                    logger.error("Failed to load connection: {}", file.getFileName(), e);
                }
            }
            logger.info("Loaded {} connections", connections.size());
        } catch (IOException e) {
            logger.error("Failed to read connections directory", e);
        }
    }
    
    private void loadFolders() {
        Path foldersFile = configDir.resolve("folders.json");
        
        if (Files.exists(foldersFile)) {
            try {
                String json = new String(Files.readAllBytes(foldersFile), StandardCharsets.UTF_8);
                folders = JSON.parseArray(json, FolderConfig.class);
                if (folders == null) {
                    folders = new ArrayList<>();
                }
                logger.info("Loaded {} folders", folders.size());
            } catch (Exception e) {
                logger.error("Failed to load folders", e);
                folders = new ArrayList<>();
            }
        }
    }
    
    /**
     * 初始化配置管理器（用于延迟初始化）
     */
    public void init() {
        // Already initialized in constructor, but this method
        // can be called to reinitialize if needed
        logger.info("ConfigManager initialized");
    }
    
    // Event listener management
    
    public void addConfigChangeListener(ConfigChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeConfigChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void fireConfigChanged(String type, String configId) {
        for (ConfigChangeListener listener : listeners) {
            try {
                listener.onConfigChanged(type, configId);
            } catch (Exception e) {
                logger.error("Error notifying config listener", e);
            }
        }
    }
    
    public void saveAll() {
        saveAppConfig();
        saveAllConnections();
        saveFolders();
    }
    
    public void saveConfig() {
        saveAll();
    }
    
    public File getConfigDir() {
        return configDir.toFile();
    }
    
    public String getBackupDir() {
        return backupDir.toString();
    }
    
    public void backupConfigs() throws IOException {
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        Path backupFile = backupDir.resolve("backup_" + timestamp + ".json");
        exportToFile(backupFile);
    }
    
    public void exportConnection(String id, File file) throws IOException {
        ConnectConfig config = connections.get(id);
        if (config != null) {
            String json = JSON.toJSONString(config, SerializerFeature.PrettyFormat);
            Files.write(file.toPath(), json.getBytes(StandardCharsets.UTF_8));
        }
    }
    
    public void saveAppConfig() {
        Path configFile = configDir.resolve("config.json");
        
        try {
            String json = JSON.toJSONString(appConfig, 
                SerializerFeature.PrettyFormat, 
                SerializerFeature.WriteMapNullValue);
            Files.write(configFile, json.getBytes(StandardCharsets.UTF_8));
            logger.debug("App config saved");
        } catch (IOException e) {
            logger.error("Failed to save app config", e);
        }
    }
    
    public void saveConnection(ConnectConfig config) {
        if (config.getId() == null) {
            config.setId(UUID.randomUUID().toString());
        }
        
        connections.put(config.getId(), config);
        
        Path file = connectDir.resolve(config.getId() + ".json");
        try {
            // Encrypt password before saving
            ConnectConfig toSave = config.clone();
            if (toSave.getPassword() != null && !toSave.getPassword().isEmpty()) {
                toSave.setPassword(EncryptUtil.encryptDES(toSave.getPassword()));
            }
            
            String json = JSON.toJSONString(toSave, 
                SerializerFeature.PrettyFormat);
            Files.write(file, json.getBytes(StandardCharsets.UTF_8));
            logger.debug("Connection saved: {}", config.getName());
        } catch (Exception e) {
            logger.error("Failed to save connection", e);
        }
    }
    
    private void saveAllConnections() {
        for (ConnectConfig config : connections.values()) {
            saveConnection(config);
        }
    }
    
    public void saveFolders() {
        Path foldersFile = configDir.resolve("folders.json");
        
        try {
            String json = JSON.toJSONString(folders, 
                SerializerFeature.PrettyFormat);
            Files.write(foldersFile, json.getBytes(StandardCharsets.UTF_8));
            logger.debug("Folders saved");
        } catch (IOException e) {
            logger.error("Failed to save folders", e);
        }
    }
    
    public void deleteConnection(String id) {
        connections.remove(id);
        Path file = connectDir.resolve(id + ".json");
        try {
            Files.deleteIfExists(file);
            logger.info("Connection deleted: {}", id);
        } catch (IOException e) {
            logger.error("Failed to delete connection file", e);
        }
    }
    
    public Path getConfigDirPath() {
        return configDir;
    }
    
    public AppConfig getAppConfig() {
        return appConfig;
    }
    
    public Map<String, ConnectConfig> getConnections() {
        return Collections.unmodifiableMap(connections);
    }
    
    public List<ConnectConfig> getAllConnections() {
        return new ArrayList<>(connections.values());
    }
    
    public ConnectConfig getConnection(String id) {
        return connections.get(id);
    }
    
    // Alias method for compatibility
    public ConnectConfig getConnectionById(String id) {
        return getConnection(id);
    }
    
    public List<FolderConfig> getFolders() {
        return folders;
    }
    
    public FolderConfig getFolderById(String id) {
        return foldersById.get(id);
    }
    
    public void addFolder(FolderConfig folder) {
        if (folder.getId() == null) {
            folder.setId(UUID.randomUUID().toString());
        }
        folder.setCreateTime(System.currentTimeMillis());
        folder.setUpdateTime(System.currentTimeMillis());
        folders.add(folder);
        foldersById.put(folder.getId(), folder);
        saveFolders();
        fireConfigChanged("folder_add", folder.getId());
    }
    
    public FolderConfig createFolder(String parentId, String name) {
        FolderConfig folder = new FolderConfig();
        folder.setId(UUID.randomUUID().toString());
        folder.setName(name);
        folder.setParentId(parentId != null ? parentId : "root");
        folder.setCreateTime(System.currentTimeMillis());
        folder.setUpdateTime(System.currentTimeMillis());
        
        folders.add(folder);
        foldersById.put(folder.getId(), folder);
        saveFolders();
        fireConfigChanged("folder_add", folder.getId());
        
        return folder;
    }
    
    public void updateFolder(FolderConfig folder) {
        folder.setUpdateTime(System.currentTimeMillis());
        foldersById.put(folder.getId(), folder);
        saveFolders();
        fireConfigChanged("folder_update", folder.getId());
    }
    
    public void removeFolder(String id) {
        folders.removeIf(f -> id.equals(f.getId()));
        foldersById.remove(id);
        saveFolders();
        fireConfigChanged("folder_delete", id);
    }
    
    public void moveFolder(String folderId, String newParentId) {
        FolderConfig folder = foldersById.get(folderId);
        if (folder != null) {
            folder.setParentId(newParentId != null ? newParentId : "root");
            folder.setUpdateTime(System.currentTimeMillis());
            saveFolders();
            fireConfigChanged("folder_move", folderId);
        }
    }
    
    public void backup() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Path backupFile = backupDir.resolve("backup_" + timestamp + ".zip");
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(backupFile))) {
            // 备份连接配置
            Path connectFile = configDir.resolve("connect.json");
            if (Files.exists(connectFile)) {
                addToZip(zos, connectFile, "connect.json");
            }
            
            // 备份文件夹配置
            Path folderFile = configDir.resolve("folder.json");
            if (Files.exists(folderFile)) {
                addToZip(zos, folderFile, "folder.json");
            }
            
            // 备份应用配置
            Path appFile = configDir.resolve("app.json");
            if (Files.exists(appFile)) {
                addToZip(zos, appFile, "app.json");
            }
            
            // 备份快捷命令
            Path commandFile = configDir.resolve("commands.json");
            if (Files.exists(commandFile)) {
                addToZip(zos, commandFile, "commands.json");
            }
            
            // 备份热键配置
            Path hotkeyFile = configDir.resolve("hotkeys.json");
            if (Files.exists(hotkeyFile)) {
                addToZip(zos, hotkeyFile, "hotkeys.json");
            }
            
            logger.info("Backup created: {}", backupFile);
            
        } catch (IOException e) {
            logger.error("Backup failed", e);
        }
    }
    
    private void addToZip(ZipOutputStream zos, Path file, String entryName) throws IOException {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
        Files.copy(file, zos);
        zos.closeEntry();
    }
    
    // Recent connections
    
    public List<ConnectConfig> getRecentConnections(int limit) {
        return connections.values().stream()
            .sorted((a, b) -> Long.compare(b.getLastConnectTime(), a.getLastConnectTime()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public List<ConnectConfig> getConnectionsSortedByCreateTime() {
        return connections.values().stream()
            .sorted((a, b) -> Long.compare(b.getCreateTime(), a.getCreateTime()))
            .collect(Collectors.toList());
    }
    
    public void updateConnectionTime(String id) {
        ConnectConfig config = connections.get(id);
        if (config != null) {
            config.setLastConnectTime(System.currentTimeMillis());
            saveConnection(config);
        }
    }
    
    // Connection by folder
    
    public List<ConnectConfig> getConnectionsByFolder(String folderId) {
        String parentId = folderId != null ? folderId : "root";
        return connections.values().stream()
            .filter(c -> parentId.equals(c.getParentId()))
            .collect(Collectors.toList());
    }
    
    public List<FolderConfig> getChildFolders(String parentId) {
        String pid = parentId != null ? parentId : "root";
        return folders.stream()
            .filter(f -> pid.equals(f.getParentId()))
            .collect(Collectors.toList());
    }
    
    public void moveConnection(String configId, String newParentId) {
        ConnectConfig config = connections.get(configId);
        if (config != null) {
            config.setParentId(newParentId != null ? newParentId : "root");
            config.setUpdateTime(System.currentTimeMillis());
            saveConnection(config);
            fireConfigChanged("connection_move", configId);
        }
    }
    
    // Import/Export
    
    public void importConnections(File file) throws IOException {
        importFromFile(file.toPath(), false);
    }
    
    public void exportConnections(File file) throws IOException {
        exportToFile(file.toPath());
    }
    
    public void exportToFile(Path exportFile) throws IOException {
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("version", "1.0");
        exportData.put("exportTime", System.currentTimeMillis());
        exportData.put("connections", new ArrayList<>(connections.values()));
        exportData.put("folders", folders);
        
        String json = JSON.toJSONString(exportData, SerializerFeature.PrettyFormat);
        Files.write(exportFile, json.getBytes(StandardCharsets.UTF_8));
        logger.info("Exported {} connections and {} folders to {}", 
            connections.size(), folders.size(), exportFile);
    }
    
    public int importFromFile(Path importFile, boolean overwrite) throws IOException {
        String json = new String(Files.readAllBytes(importFile), StandardCharsets.UTF_8);
        com.alibaba.fastjson.JSONObject data = JSON.parseObject(json);
        
        int imported = 0;
        
        // Import folders first
        com.alibaba.fastjson.JSONArray foldersArray = data.getJSONArray("folders");
        if (foldersArray != null) {
            for (int i = 0; i < foldersArray.size(); i++) {
                FolderConfig folder = foldersArray.getObject(i, FolderConfig.class);
                if (folder != null && folder.getId() != null) {
                    if (overwrite || !foldersById.containsKey(folder.getId())) {
                        folders.add(folder);
                        foldersById.put(folder.getId(), folder);
                    }
                }
            }
        }
        
        // Import connections
        com.alibaba.fastjson.JSONArray connectionsArray = data.getJSONArray("connections");
        if (connectionsArray != null) {
            for (int i = 0; i < connectionsArray.size(); i++) {
                ConnectConfig config = connectionsArray.getObject(i, ConnectConfig.class);
                if (config != null && config.getId() != null) {
                    if (overwrite || !connections.containsKey(config.getId())) {
                        connections.put(config.getId(), config);
                        saveConnection(config);
                        imported++;
                    }
                }
            }
        }
        
        saveFolders();
        fireConfigChanged("import", null);
        logger.info("Imported {} connections from {}", imported, importFile);
        return imported;
    }
    
    // Restore from backup
    
    public void restoreFromBackup(Path backupFile) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(backupFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path targetPath = configDir.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
        
        // Reload all configs
        connections.clear();
        connectionsByPath.clear();
        folders.clear();
        foldersById.clear();
        loadAllConfigs();
        
        fireConfigChanged("restore", null);
        logger.info("Restored from backup: {}", backupFile);
    }
    
    // List backups
    
    public List<Path> listBackups() throws IOException {
        if (!Files.exists(backupDir)) {
            return Collections.emptyList();
        }
        return Files.list(backupDir)
            .filter(p -> p.toString().endsWith(".zip"))
            .sorted((a, b) -> {
                try {
                    return Files.getLastModifiedTime(b).compareTo(Files.getLastModifiedTime(a));
                } catch (IOException e) {
                    return 0;
                }
            })
            .collect(Collectors.toList());
    }
    
    // Reload configs
    
    public void reload() {
        connections.clear();
        connectionsByPath.clear();
        folders.clear();
        foldersById.clear();
        loadAllConfigs();
        fireConfigChanged("reload", null);
    }
    
    // Config change listener interface
    
    public interface ConfigChangeListener {
        void onConfigChanged(String changeType, String configId);
    }
}
