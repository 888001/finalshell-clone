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
import java.util.zip.ZipEntry;
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
    private List<FolderConfig> folders = new ArrayList<>();
    
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
    
    public List<FolderConfig> getFolders() {
        return folders;
    }
    
    public void addFolder(FolderConfig folder) {
        if (folder.getId() == null) {
            folder.setId(UUID.randomUUID().toString());
        }
        folders.add(folder);
        saveFolders();
    }
    
    public void removeFolder(String id) {
        folders.removeIf(f -> id.equals(f.getId()));
        saveFolders();
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
}
