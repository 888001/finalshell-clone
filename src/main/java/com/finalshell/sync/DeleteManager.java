package com.finalshell.sync;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 删除管理器
 * 管理同步过程中的删除记录
 */
public class DeleteManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteManager.class);
    private static DeleteManager instance;
    private List<DeleteRecord> deleteRecords;
    private File recordFile;
    private boolean autoSave;
    
    private DeleteManager() {
        this.deleteRecords = new CopyOnWriteArrayList<>();
        this.autoSave = true;
    }
    
    public static synchronized DeleteManager getInstance() {
        if (instance == null) {
            instance = new DeleteManager();
        }
        return instance;
    }
    
    public void setRecordFile(File file) {
        this.recordFile = file;
        loadRecords();
    }
    
    public void addRecord(DeleteRecord record) {
        deleteRecords.add(record);
        if (autoSave) {
            saveRecords();
        }
    }
    
    public void removeRecord(DeleteRecord record) {
        deleteRecords.remove(record);
        if (autoSave) {
            saveRecords();
        }
    }
    
    public void removeRecord(String id) {
        deleteRecords.removeIf(r -> r.getId().equals(id));
        if (autoSave) {
            saveRecords();
        }
    }
    
    public List<DeleteRecord> getRecords() {
        return new ArrayList<>(deleteRecords);
    }
    
    public List<DeleteRecord> getRecordsByType(String type) {
        List<DeleteRecord> result = new ArrayList<>();
        for (DeleteRecord record : deleteRecords) {
            if (type.equals(record.getType())) {
                result.add(record);
            }
        }
        return result;
    }
    
    public DeleteRecord getRecord(String id) {
        for (DeleteRecord record : deleteRecords) {
            if (id.equals(record.getId())) {
                return record;
            }
        }
        return null;
    }
    
    public boolean hasRecord(String id) {
        return getRecord(id) != null;
    }
    
    public void clearRecords() {
        deleteRecords.clear();
        if (autoSave) {
            saveRecords();
        }
    }
    
    public void clearRecordsByType(String type) {
        deleteRecords.removeIf(r -> type.equals(r.getType()));
        if (autoSave) {
            saveRecords();
        }
    }
    
    private void loadRecords() {
        if (recordFile == null || !recordFile.exists()) {
            return;
        }
        try {
            String content = new String(Files.readAllBytes(recordFile.toPath()), StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(content);
            deleteRecords.clear();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                DeleteRecord record = new DeleteRecord();
                record.setId(obj.getString("id"));
                record.setType(obj.getString("type"));
                record.setPath(obj.getString("path"));
                record.setTimestamp(obj.getLongValue("timestamp"));
                deleteRecords.add(record);
            }
            logger.info("加载删除记录: {} 条", deleteRecords.size());
        } catch (Exception e) {
            logger.error("加载删除记录失败", e);
        }
    }
    
    public void saveRecords() {
        if (recordFile == null) {
            return;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (DeleteRecord record : deleteRecords) {
                JSONObject obj = new JSONObject();
                obj.put("id", record.getId());
                obj.put("type", record.getType());
                obj.put("path", record.getPath());
                obj.put("timestamp", record.getTimestamp());
                jsonArray.add(obj);
            }
            Files.write(recordFile.toPath(), JSON.toJSONString(jsonArray, true).getBytes(StandardCharsets.UTF_8));
            logger.debug("保存删除记录: {} 条", deleteRecords.size());
        } catch (Exception e) {
            logger.error("保存删除记录失败", e);
        }
    }
    
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    public boolean isAutoSave() {
        return autoSave;
    }
    
    public int getRecordCount() {
        return deleteRecords.size();
    }
    
    /**
     * 关闭管理器，保存所有记录
     */
    public void shutdown() {
        saveRecords();
    }
}
