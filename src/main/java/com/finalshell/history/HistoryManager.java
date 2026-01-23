package com.finalshell.history;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 历史记录管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: Sync_DeepAnalysis.md - HistoryManager
 */
public class HistoryManager {
    
    private static final Logger logger = LoggerFactory.getLogger(HistoryManager.class);
    private static final int MAX_CMD_HISTORY = 1000;
    private static final int MAX_FILE_HISTORY = 500;
    private static final int MAX_PATH_HISTORY = 100;
    
    private static Path dataDir;
    private static final List<String> cmdHistory = new CopyOnWriteArrayList<>();
    private static final List<String> fileHistory = new CopyOnWriteArrayList<>();
    private static final List<String> pathHistory = new CopyOnWriteArrayList<>();
    
    /**
     * 初始化
     */
    public static void initialize(Path dir) {
        dataDir = dir;
        loadHistory();
    }
    
    /**
     * 添加命令历史
     */
    public static void addCmdHistory(String cmd) {
        if (cmd == null || cmd.trim().isEmpty()) return;
        
        // 移除重复
        cmdHistory.remove(cmd);
        cmdHistory.add(0, cmd);
        
        // 限制大小
        while (cmdHistory.size() > MAX_CMD_HISTORY) {
            cmdHistory.remove(cmdHistory.size() - 1);
        }
        
        saveHistory();
    }
    
    /**
     * 添加文件历史
     */
    public static void addFileHistory(String path) {
        if (path == null || path.trim().isEmpty()) return;
        
        fileHistory.remove(path);
        fileHistory.add(0, path);
        
        while (fileHistory.size() > MAX_FILE_HISTORY) {
            fileHistory.remove(fileHistory.size() - 1);
        }
        
        saveHistory();
    }
    
    /**
     * 添加路径历史
     */
    public static void addPathHistory(String path) {
        if (path == null || path.trim().isEmpty()) return;
        
        pathHistory.remove(path);
        pathHistory.add(0, path);
        
        while (pathHistory.size() > MAX_PATH_HISTORY) {
            pathHistory.remove(pathHistory.size() - 1);
        }
        
        saveHistory();
    }
    
    /**
     * 获取命令历史
     */
    public static List<String> getCmdHistory() {
        return new ArrayList<>(cmdHistory);
    }
    
    /**
     * 获取文件历史
     */
    public static List<String> getFileHistory() {
        return new ArrayList<>(fileHistory);
    }
    
    /**
     * 获取路径历史
     */
    public static List<String> getPathHistory() {
        return new ArrayList<>(pathHistory);
    }
    
    /**
     * 搜索命令历史
     */
    public static List<String> searchCmdHistory(String keyword) {
        List<String> result = new ArrayList<>();
        String lower = keyword.toLowerCase();
        
        for (String cmd : cmdHistory) {
            if (cmd.toLowerCase().contains(lower)) {
                result.add(cmd);
            }
        }
        
        return result;
    }
    
    /**
     * 清空命令历史
     */
    public static void clearCmdHistory() {
        cmdHistory.clear();
        saveHistory();
    }
    
    /**
     * 清空文件历史
     */
    public static void clearFileHistory() {
        fileHistory.clear();
        saveHistory();
    }
    
    /**
     * 导入命令历史
     */
    public static void importCmdHistory(JSONArray arr) {
        if (arr == null) return;
        
        for (int i = 0; i < arr.size(); i++) {
            String cmd = arr.getString(i);
            if (cmd != null && !cmd.isEmpty() && !cmdHistory.contains(cmd)) {
                cmdHistory.add(cmd);
            }
        }
        
        while (cmdHistory.size() > MAX_CMD_HISTORY) {
            cmdHistory.remove(cmdHistory.size() - 1);
        }
    }
    
    /**
     * 导入文件历史
     */
    public static void importFileHistory(JSONArray arr) {
        if (arr == null) return;
        
        for (int i = 0; i < arr.size(); i++) {
            String path = arr.getString(i);
            if (path != null && !path.isEmpty() && !fileHistory.contains(path)) {
                fileHistory.add(path);
            }
        }
        
        while (fileHistory.size() > MAX_FILE_HISTORY) {
            fileHistory.remove(fileHistory.size() - 1);
        }
    }
    
    /**
     * 导出命令历史
     */
    public static JSONArray exportCmdHistory() {
        JSONArray arr = new JSONArray();
        arr.addAll(cmdHistory);
        return arr;
    }
    
    /**
     * 导出文件历史
     */
    public static JSONArray exportFileHistory() {
        JSONArray arr = new JSONArray();
        arr.addAll(fileHistory);
        return arr;
    }
    
    /**
     * 加载历史记录
     */
    private static void loadHistory() {
        if (dataDir == null) return;
        
        try {
            Path historyFile = dataDir.resolve("history.json");
            if (Files.exists(historyFile)) {
                String content = new String(Files.readAllBytes(historyFile), "UTF-8");
                JSONObject json = JSONObject.parseObject(content);
                
                if (json != null) {
                    JSONArray cmds = json.getJSONArray("cmd_history");
                    if (cmds != null) {
                        cmdHistory.clear();
                        for (int i = 0; i < cmds.size(); i++) {
                            cmdHistory.add(cmds.getString(i));
                        }
                    }
                    
                    JSONArray files = json.getJSONArray("file_history");
                    if (files != null) {
                        fileHistory.clear();
                        for (int i = 0; i < files.size(); i++) {
                            fileHistory.add(files.getString(i));
                        }
                    }
                    
                    JSONArray paths = json.getJSONArray("path_history");
                    if (paths != null) {
                        pathHistory.clear();
                        for (int i = 0; i < paths.size(); i++) {
                            pathHistory.add(paths.getString(i));
                        }
                    }
                }
                
                logger.info("加载历史记录: {} 命令, {} 文件, {} 路径", 
                    cmdHistory.size(), fileHistory.size(), pathHistory.size());
            }
        } catch (Exception e) {
            logger.error("加载历史记录失败", e);
        }
    }
    
    /**
     * 保存历史记录
     */
    private static void saveHistory() {
        if (dataDir == null) return;
        
        try {
            JSONObject json = new JSONObject();
            json.put("cmd_history", exportCmdHistory());
            json.put("file_history", exportFileHistory());
            
            JSONArray pathArr = new JSONArray();
            pathArr.addAll(pathHistory);
            json.put("path_history", pathArr);
            
            Path historyFile = dataDir.resolve("history.json");
            Files.write(historyFile, json.toJSONString().getBytes("UTF-8"));
        } catch (Exception e) {
            logger.error("保存历史记录失败", e);
        }
    }
}
