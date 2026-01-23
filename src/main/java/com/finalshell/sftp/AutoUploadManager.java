package com.finalshell.sftp;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * 自动上传管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class AutoUploadManager {
    
    private static AutoUploadManager instance;
    
    private Map<String, WatchService> watchServices = new ConcurrentHashMap<>();
    private Map<String, AutoUploadConfig> configs = new ConcurrentHashMap<>();
    private ExecutorService executor;
    private volatile boolean running = false;
    
    private List<AutoUploadListener> listeners = new ArrayList<>();
    
    private AutoUploadManager() {
        executor = Executors.newCachedThreadPool();
    }
    
    public static synchronized AutoUploadManager getInstance() {
        if (instance == null) {
            instance = new AutoUploadManager();
        }
        return instance;
    }
    
    public void addWatch(String id, File localDir, String remotePath, FtpClient ftpClient) {
        if (watchServices.containsKey(id)) {
            return;
        }
        
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = localDir.toPath();
            path.register(watchService, 
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY);
            
            watchServices.put(id, watchService);
            
            AutoUploadConfig config = new AutoUploadConfig();
            config.setId(id);
            config.setLocalDir(localDir);
            config.setRemotePath(remotePath);
            config.setFtpClient(ftpClient);
            configs.put(id, config);
            
            if (running) {
                startWatch(id);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void removeWatch(String id) {
        WatchService watchService = watchServices.remove(id);
        if (watchService != null) {
            try {
                watchService.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        configs.remove(id);
    }
    
    public void start() {
        if (running) {
            return;
        }
        running = true;
        
        for (String id : watchServices.keySet()) {
            startWatch(id);
        }
    }
    
    private void startWatch(String id) {
        WatchService watchService = watchServices.get(id);
        AutoUploadConfig config = configs.get(id);
        
        if (watchService == null || config == null) {
            return;
        }
        
        executor.submit(() -> {
            while (running && watchServices.containsKey(id)) {
                try {
                    WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                    if (key == null) {
                        continue;
                    }
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        
                        Path fileName = (Path) event.context();
                        File file = new File(config.getLocalDir(), fileName.toString());
                        
                        if (file.exists() && file.isFile()) {
                            uploadFile(config, file);
                        }
                    }
                    
                    key.reset();
                    
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void uploadFile(AutoUploadConfig config, File file) {
        try {
            FtpClient client = config.getFtpClient();
            if (client != null && client.isConnected()) {
                String remotePath = config.getRemotePath() + "/" + file.getName();
                client.upload(file.getAbsolutePath(), remotePath);
                
                notifyUploaded(file, remotePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public void shutdown() {
        stop();
        
        for (WatchService watchService : watchServices.values()) {
            try {
                watchService.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        watchServices.clear();
        configs.clear();
        
        executor.shutdown();
    }
    
    public void addListener(AutoUploadListener listener) {
        listeners.add(listener);
    }
    
    private void notifyUploaded(File file, String remotePath) {
        for (AutoUploadListener listener : listeners) {
            listener.onUploaded(file, remotePath);
        }
    }
    
    public interface AutoUploadListener {
        void onUploaded(File localFile, String remotePath);
    }
    
    public static class AutoUploadConfig {
        private String id;
        private File localDir;
        private String remotePath;
        private FtpClient ftpClient;
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public File getLocalDir() { return localDir; }
        public void setLocalDir(File localDir) { this.localDir = localDir; }
        public String getRemotePath() { return remotePath; }
        public void setRemotePath(String remotePath) { this.remotePath = remotePath; }
        public FtpClient getFtpClient() { return ftpClient; }
        public void setFtpClient(FtpClient ftpClient) { this.ftpClient = ftpClient; }
    }
}
