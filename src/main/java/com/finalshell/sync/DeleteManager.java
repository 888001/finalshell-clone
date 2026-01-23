package com.finalshell.sync;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 删除管理器
 * 管理同步过程中的删除记录
 */
public class DeleteManager {
    
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
        // 从文件加载记录
    }
    
    public void saveRecords() {
        if (recordFile == null) {
            return;
        }
        // 保存记录到文件
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
}
