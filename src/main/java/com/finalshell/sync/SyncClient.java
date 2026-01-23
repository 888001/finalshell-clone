package com.finalshell.sync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.finalshell.util.HttpTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * 云同步客户端
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Sync_DeepAnalysis.md - SyncClient
 */
public class SyncClient {
    
    private static final Logger logger = LoggerFactory.getLogger(SyncClient.class);
    
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_ERROR = 2;
    public static final int CODE_RDP = 3;
    
    private SyncConfig syncConfig;
    private int code;
    private int responseCode;
    private boolean initialized = false;
    private boolean expired = false;
    private long expiredTime;
    
    private final List<SyncListener> listeners = new ArrayList<>();
    
    public SyncClient(SyncConfig config) {
        this.syncConfig = config;
    }
    
    /**
     * 初始化同步客户端
     */
    public void initialize() throws Exception {
        if (syncConfig == null) {
            throw new Exception("同步配置为空");
        }
        
        if (!syncConfig.isEnabled()) {
            throw new Exception("同步功能未启用");
        }
        
        initialized = true;
        logger.info("同步客户端初始化完成");
    }
    
    /**
     * 上传同步数据
     */
    public void upload(JSONObject data) throws Exception {
        checkInitialized();
        
        notifyListeners(SyncEvent.UPLOAD_START, "开始上传同步数据");
        
        try {
            JSONObject request = new JSONObject();
            request.put("command", "upload_sync_file");
            request.put("username", syncConfig.getUsername());
            request.put("password", syncConfig.getPassword());
            request.put("data", data.toJSONString());
            
            JSONObject response = HttpTools.postJson(syncConfig.getSyncServerUrl(), request);
            
            if (response != null && response.getIntValue("code") == CODE_SUCCESS) {
                code = CODE_SUCCESS;
                notifyListeners(SyncEvent.UPLOAD_SUCCESS, "同步数据上传成功");
            } else {
                code = CODE_ERROR;
                String error = response != null ? response.getString("message") : "上传失败";
                notifyListeners(SyncEvent.UPLOAD_ERROR, error);
                throw new Exception(error);
            }
        } catch (Exception e) {
            code = CODE_ERROR;
            notifyListeners(SyncEvent.UPLOAD_ERROR, e.getMessage());
            throw e;
        }
    }
    
    /**
     * 下载同步数据
     */
    public JSONObject download() throws Exception {
        checkInitialized();
        
        notifyListeners(SyncEvent.DOWNLOAD_START, "开始下载同步数据");
        
        try {
            JSONObject request = new JSONObject();
            request.put("command", "download_sync_file");
            request.put("username", syncConfig.getUsername());
            request.put("password", syncConfig.getPassword());
            
            JSONObject response = HttpTools.postJson(syncConfig.getSyncServerUrl(), request);
            
            if (response != null && response.getIntValue("code") == CODE_SUCCESS) {
                code = CODE_SUCCESS;
                notifyListeners(SyncEvent.DOWNLOAD_SUCCESS, "同步数据下载成功");
                return response.getJSONObject("data");
            } else {
                code = CODE_ERROR;
                String error = response != null ? response.getString("message") : "下载失败";
                notifyListeners(SyncEvent.DOWNLOAD_ERROR, error);
                throw new Exception(error);
            }
        } catch (Exception e) {
            code = CODE_ERROR;
            notifyListeners(SyncEvent.DOWNLOAD_ERROR, e.getMessage());
            throw e;
        }
    }
    
    /**
     * 获取同步文件列表
     */
    public List<SyncFileInfo> getFileList() throws Exception {
        checkInitialized();
        
        JSONObject request = new JSONObject();
        request.put("command", "get_file_list");
        request.put("username", syncConfig.getUsername());
        request.put("password", syncConfig.getPassword());
        
        JSONObject response = HttpTools.postJson(syncConfig.getSyncServerUrl(), request);
        
        List<SyncFileInfo> fileList = new ArrayList<>();
        if (response != null && response.getIntValue("code") == CODE_SUCCESS) {
            JSONArray files = response.getJSONArray("files");
            if (files != null) {
                for (int i = 0; i < files.size(); i++) {
                    JSONObject file = files.getJSONObject(i);
                    SyncFileInfo info = new SyncFileInfo();
                    info.setFileName(file.getString("name"));
                    info.setFileSize(file.getLongValue("size"));
                    info.setLastModified(file.getLongValue("modified"));
                    fileList.add(info);
                }
            }
        }
        
        return fileList;
    }
    
    private void checkInitialized() throws Exception {
        if (!initialized) {
            throw new Exception("同步客户端未初始化");
        }
    }
    
    public void addListener(SyncListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(SyncListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(int eventType, String message) {
        SyncEvent event = new SyncEvent(eventType, message);
        for (SyncListener listener : listeners) {
            listener.onSyncEvent(event);
        }
    }
    
    // Getters
    public int getCode() { return code; }
    public int getResponseCode() { return responseCode; }
    public boolean isInitialized() { return initialized; }
    public boolean isExpired() { return expired; }
    public long getExpiredTime() { return expiredTime; }
    
    /**
     * 同步文件信息
     */
    public static class SyncFileInfo {
        private String fileName;
        private long fileSize;
        private long lastModified;
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public long getFileSize() { return fileSize; }
        public void setFileSize(long fileSize) { this.fileSize = fileSize; }
        public long getLastModified() { return lastModified; }
        public void setLastModified(long lastModified) { this.lastModified = lastModified; }
    }
    
    /**
     * 同步事件
     */
    public static class SyncEvent {
        public static final int UPLOAD_START = 1;
        public static final int UPLOAD_SUCCESS = 2;
        public static final int UPLOAD_ERROR = 3;
        public static final int DOWNLOAD_START = 4;
        public static final int DOWNLOAD_SUCCESS = 5;
        public static final int DOWNLOAD_ERROR = 6;
        
        private final int type;
        private final String message;
        
        public SyncEvent(int type, String message) {
            this.type = type;
            this.message = message;
        }
        
        public int getType() { return type; }
        public String getMessage() { return message; }
    }
    
    /**
     * 同步监听器
     */
    public interface SyncListener {
        void onSyncEvent(SyncEvent event);
    }
}
