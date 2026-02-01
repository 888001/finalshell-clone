package com.finalshell.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JOptionPane;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件排序配置 - 对齐原版myssh实现
 * 
 * Based on analysis of myssh/FileSortConfig.java (136行)
 * 支持JSON持久化和文件顺序管理
 */
public class FileSortConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(FileSortConfig.class);
    
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_SIZE = 1;
    public static final int SORT_BY_TIME = 2;
    public static final int SORT_BY_TYPE = 3;
    
    private String configFilePath;
    private Map<String, Integer> fileOrderMap = new HashMap<>();
    
    private int sortBy = SORT_BY_NAME;
    private boolean ascending = true;
    private boolean foldersFirst = true;
    private boolean showHiddenFiles = false;
    private boolean caseSensitive = false;
    
    public FileSortConfig() {}
    
    public FileSortConfig(String configFilePath) {
        this.configFilePath = configFilePath;
    }
    
    public FileSortConfig(int sortBy, boolean ascending) {
        this.sortBy = sortBy;
        this.ascending = ascending;
    }
    
    /**
     * 从JSON文件加载配置 - 对齐原版myssh
     */
    public void loadConfig() throws Exception {
        if (configFilePath == null) return;
        
        String content = readFileContent(configFilePath);
        JSONObject json = JSONObject.parseObject(content);
        JSONArray fileList = json.getJSONArray("file_list");
        
        if (fileList != null) {
            for (int i = 0; i < fileList.size(); i++) {
                JSONObject fileInfo = fileList.getJSONObject(i);
                String path = fileInfo.getString("path");
                int order = fileInfo.getIntValue("order");
                
                // 验证文件是否存在（对齐原版逻辑）
                File file = new File(path);
                if (file.exists()) {
                    fileOrderMap.put(path, order);
                }
            }
        }
        logger.info("已加载文件排序配置: {} 个文件", fileOrderMap.size());
    }
    
    /**
     * 保存配置到JSON文件 - 对齐原版myssh
     */
    public void saveConfig() throws Exception {
        if (configFilePath == null) return;
        
        JSONObject json = new JSONObject();
        JSONArray fileList = new JSONArray();
        json.put("file_list", fileList);
        
        for (Map.Entry<String, Integer> entry : fileOrderMap.entrySet()) {
            JSONObject fileInfo = new JSONObject();
            fileInfo.put("path", entry.getKey());
            fileInfo.put("order", entry.getValue());
            fileList.add(fileInfo);
        }
        
        writeFileContent(json.toJSONString().getBytes(StandardCharsets.UTF_8), configFilePath);
        logger.info("已保存文件排序配置: {} 个文件", fileOrderMap.size());
    }
    
    /**
     * 设置文件顺序
     */
    public void setFileOrder(String path, int order) {
        fileOrderMap.put(path, order);
    }
    
    /**
     * 获取文件顺序
     */
    public int getFileOrder(File file) {
        String path = file.getAbsolutePath();
        return fileOrderMap.getOrDefault(path, 0);
    }
    
    /**
     * 写入文件内容 - 对齐原版myssh实现
     */
    private void writeFileContent(byte[] data, String path) throws Exception {
        File dir = new File(path).getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        
        String systemName = System.getProperty("os.name").toLowerCase();
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        } catch (Exception e) {
            if (systemName.contains("windows")) {
                JOptionPane.showMessageDialog(null, 
                    "保存配置文件失败,请以管理员身份运行! " + path);
            }
            throw e;
        }
    }
    
    /**
     * 读取文件内容 - 对齐原版myssh实现
     */
    public static String readFileContent(String path) throws Exception {
        String str = null;
        FileInputStream fis = null;
        DataInputStream dis = null;
        
        try {
            File file = new File(path);
            int length = (int) file.length();
            byte[] data = new byte[length];
            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            dis.readFully(data);
            str = new String(data, StandardCharsets.UTF_8);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("关闭文件流失败", e);
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    logger.error("关闭数据流失败", e);
                }
            }
        }
        return str;
    }
    
    public int compare(String name1, long size1, long time1, boolean isDir1,
                       String name2, long size2, long time2, boolean isDir2) {
        // 目录优先
        if (foldersFirst && isDir1 != isDir2) {
            return isDir1 ? -1 : 1;
        }
        
        int result;
        switch (sortBy) {
            case SORT_BY_SIZE:
                result = Long.compare(size1, size2);
                break;
            case SORT_BY_TIME:
                result = Long.compare(time1, time2);
                break;
            case SORT_BY_TYPE:
                String ext1 = getExtension(name1);
                String ext2 = getExtension(name2);
                result = caseSensitive ? ext1.compareTo(ext2) : 
                    ext1.compareToIgnoreCase(ext2);
                if (result == 0) {
                    result = caseSensitive ? name1.compareTo(name2) :
                        name1.compareToIgnoreCase(name2);
                }
                break;
            case SORT_BY_NAME:
            default:
                result = caseSensitive ? name1.compareTo(name2) :
                    name1.compareToIgnoreCase(name2);
                break;
        }
        
        return ascending ? result : -result;
    }
    
    private String getExtension(String name) {
        int idx = name.lastIndexOf('.');
        return idx > 0 ? name.substring(idx + 1) : "";
    }
    
    public void toggleSort(int newSortBy) {
        if (sortBy == newSortBy) {
            ascending = !ascending;
        } else {
            sortBy = newSortBy;
            ascending = true;
        }
    }
    
    // Getters and Setters
    public int getSortBy() { return sortBy; }
    public void setSortBy(int sortBy) { this.sortBy = sortBy; }
    
    public boolean isAscending() { return ascending; }
    public void setAscending(boolean ascending) { this.ascending = ascending; }
    
    public boolean isFoldersFirst() { return foldersFirst; }
    public void setFoldersFirst(boolean foldersFirst) { this.foldersFirst = foldersFirst; }
    
    public boolean isShowHiddenFiles() { return showHiddenFiles; }
    public void setShowHiddenFiles(boolean showHiddenFiles) { this.showHiddenFiles = showHiddenFiles; }
    
    public boolean isCaseSensitive() { return caseSensitive; }
    public void setCaseSensitive(boolean caseSensitive) { this.caseSensitive = caseSensitive; }
    
    public String getSortByName() {
        switch (sortBy) {
            case SORT_BY_SIZE: return "大小";
            case SORT_BY_TIME: return "修改时间";
            case SORT_BY_TYPE: return "类型";
            default: return "名称";
        }
    }
    
    // 对齐原版myssh的getter/setter方法
    public String getConfigFilePath() {
        return configFilePath;
    }
    
    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }
    
    public Map<String, Integer> getFileOrderMap() {
        return new HashMap<>(fileOrderMap);
    }
    
    public void clearFileOrder() {
        fileOrderMap.clear();
    }
    
    public boolean hasFileOrder(String path) {
        return fileOrderMap.containsKey(path);
    }
    
    public int getFileOrderCount() {
        return fileOrderMap.size();
    }
}
