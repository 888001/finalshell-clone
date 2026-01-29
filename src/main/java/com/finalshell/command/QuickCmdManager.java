package com.finalshell.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * 快捷命令管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCmdManager {
    
    private static final Logger logger = LoggerFactory.getLogger(QuickCmdManager.class);
    private static QuickCmdManager instance;
    private List<QuickCmdGroup> groups;
    private Map<String, QuickCmd> commandMap;
    private File configFile;
    private List<QuickCmdListener> listeners;
    
    private QuickCmdManager() {
        this.groups = new ArrayList<>();
        this.commandMap = new HashMap<>();
        this.listeners = new ArrayList<>();
        loadCommands();
    }
    
    public static synchronized QuickCmdManager getInstance() {
        if (instance == null) {
            instance = new QuickCmdManager();
        }
        return instance;
    }
    
    private void loadCommands() {
        QuickCmdGroup defaultGroup = new QuickCmdGroup("默认");
        defaultGroup.addCommand(new QuickCmd("查看系统信息", "uname -a"));
        defaultGroup.addCommand(new QuickCmd("查看磁盘", "df -h"));
        defaultGroup.addCommand(new QuickCmd("查看内存", "free -h"));
        defaultGroup.addCommand(new QuickCmd("查看进程", "ps aux"));
        defaultGroup.addCommand(new QuickCmd("查看网络", "netstat -tlnp"));
        groups.add(defaultGroup);
        
        for (QuickCmdGroup group : groups) {
            for (QuickCmd cmd : group.getCommands()) {
                commandMap.put(cmd.getId(), cmd);
            }
        }
    }
    
    public void saveCommands() {
        fireCommandsChanged();
    }
    
    public List<QuickCmdGroup> getGroups() {
        return new ArrayList<>(groups);
    }
    
    public QuickCmdGroup getGroup(String id) {
        for (QuickCmdGroup group : groups) {
            if (group.getId().equals(id)) {
                return group;
            }
        }
        return null;
    }
    
    public void addGroup(QuickCmdGroup group) {
        groups.add(group);
        saveCommands();
    }
    
    public void removeGroup(QuickCmdGroup group) {
        groups.remove(group);
        for (QuickCmd cmd : group.getCommands()) {
            commandMap.remove(cmd.getId());
        }
        saveCommands();
    }
    
    public QuickCmd getCommand(String id) {
        return commandMap.get(id);
    }
    
    public void addCommand(QuickCmdGroup group, QuickCmd cmd) {
        group.addCommand(cmd);
        commandMap.put(cmd.getId(), cmd);
        saveCommands();
    }
    
    public void removeCommand(QuickCmdGroup group, QuickCmd cmd) {
        group.removeCommand(cmd);
        commandMap.remove(cmd.getId());
        saveCommands();
    }
    
    public void updateCommand(QuickCmd cmd) {
        commandMap.put(cmd.getId(), cmd);
        saveCommands();
    }
    
    public List<QuickCmd> searchCommands(String keyword) {
        List<QuickCmd> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (QuickCmd cmd : commandMap.values()) {
            if (cmd.getName().toLowerCase().contains(lowerKeyword) ||
                cmd.getCommand().toLowerCase().contains(lowerKeyword)) {
                result.add(cmd);
            }
        }
        return result;
    }
    
    public void addListener(QuickCmdListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(QuickCmdListener listener) {
        listeners.remove(listener);
    }
    
    private void fireCommandsChanged() {
        for (QuickCmdListener listener : listeners) {
            listener.onCommandsChanged();
        }
    }
    
    public interface QuickCmdListener {
        void onCommandsChanged();
    }
    
    public static void importFromFile(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(content);
            QuickCmdManager manager = getInstance();
            
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject groupObj = jsonArray.getJSONObject(i);
                String groupName = groupObj.getString("name");
                QuickCmdGroup group = new QuickCmdGroup(groupName);
                
                JSONArray cmds = groupObj.getJSONArray("commands");
                if (cmds != null) {
                    for (int j = 0; j < cmds.size(); j++) {
                        JSONObject cmdObj = cmds.getJSONObject(j);
                        QuickCmd cmd = new QuickCmd(
                            cmdObj.getString("name"),
                            cmdObj.getString("command")
                        );
                        group.addCommand(cmd);
                    }
                }
                manager.addGroup(group);
            }
            manager.saveCommands();
            logger.info("导入快捷命令成功: {}", file.getName());
        } catch (Exception e) {
            logger.error("导入快捷命令失败", e);
        }
    }
    
    public static void exportToFile(File file) {
        try {
            QuickCmdManager manager = getInstance();
            JSONArray jsonArray = new JSONArray();
            
            for (QuickCmdGroup group : manager.getGroups()) {
                JSONObject groupObj = new JSONObject();
                groupObj.put("name", group.getName());
                
                JSONArray cmds = new JSONArray();
                for (QuickCmd cmd : group.getCommands()) {
                    JSONObject cmdObj = new JSONObject();
                    cmdObj.put("name", cmd.getName());
                    cmdObj.put("command", cmd.getCommand());
                    cmds.add(cmdObj);
                }
                groupObj.put("commands", cmds);
                jsonArray.add(groupObj);
            }
            
            Files.write(file.toPath(), JSON.toJSONString(jsonArray, true).getBytes(StandardCharsets.UTF_8));
            logger.info("导出快捷命令成功: {}", file.getName());
        } catch (Exception e) {
            logger.error("导出快捷命令失败", e);
        }
    }
}
