package com.finalshell.terminal;

import com.finalshell.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Quick Command Manager - Manages saved quick commands
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCommandManager {
    
    private static final Logger logger = LoggerFactory.getLogger(QuickCommandManager.class);
    private static QuickCommandManager instance;
    
    private final List<QuickCommand> commands = new ArrayList<>();
    private final List<QuickCommandListener> listeners = new ArrayList<>();
    
    private QuickCommandManager() {
        loadCommands();
        if (commands.isEmpty()) {
            loadDefaultCommands();
        }
    }
    
    public static synchronized QuickCommandManager getInstance() {
        if (instance == null) {
            instance = new QuickCommandManager();
        }
        return instance;
    }
    
    /**
     * Load commands from config
     */
    private void loadCommands() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File file = new File(configDir, "quick_commands.dat");
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<QuickCommand> loaded = (List<QuickCommand>) ois.readObject();
                commands.addAll(loaded);
                logger.info("Loaded {} quick commands", commands.size());
            } catch (Exception e) {
                logger.warn("Failed to load quick commands: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Save commands to config
     */
    public void saveCommands() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File file = new File(configDir, "quick_commands.dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(commands));
            logger.info("Saved {} quick commands", commands.size());
        } catch (Exception e) {
            logger.error("Failed to save quick commands", e);
        }
    }
    
    /**
     * Load default commands
     */
    private void loadDefaultCommands() {
        // System info
        addCommand(new QuickCommand("top", "top", "系统进程监控"));
        addCommand(new QuickCommand("htop", "htop", "增强版进程监控"));
        addCommand(new QuickCommand("df -h", "df -h", "磁盘使用情况"));
        addCommand(new QuickCommand("free -m", "free -m", "内存使用情况"));
        addCommand(new QuickCommand("uptime", "uptime", "系统运行时间"));
        
        // Network
        addCommand(new QuickCommand("netstat -tlnp", "netstat -tlnp", "网络监听端口"));
        addCommand(new QuickCommand("ifconfig", "ifconfig", "网络接口信息"));
        addCommand(new QuickCommand("ip addr", "ip addr", "IP地址信息"));
        
        // Files
        addCommand(new QuickCommand("ls -la", "ls -la", "列出所有文件"));
        addCommand(new QuickCommand("pwd", "pwd", "当前目录"));
        addCommand(new QuickCommand("find", "find . -name ", "查找文件"));
        
        // Docker
        addCommand(new QuickCommand("docker ps", "docker ps", "Docker容器列表"));
        addCommand(new QuickCommand("docker images", "docker images", "Docker镜像列表"));
        
        // Services
        addCommand(new QuickCommand("systemctl status", "systemctl status ", "服务状态"));
        addCommand(new QuickCommand("journalctl -f", "journalctl -f", "查看日志"));
        
        logger.info("Loaded {} default quick commands", commands.size());
    }
    
    /**
     * Get all commands
     */
    public List<QuickCommand> getCommands() {
        return new ArrayList<>(commands);
    }
    
    /**
     * Get commands by category
     */
    public List<QuickCommand> getCommandsByCategory(String category) {
        List<QuickCommand> result = new ArrayList<>();
        for (QuickCommand cmd : commands) {
            if (category == null && cmd.getCategory() == null) {
                result.add(cmd);
            } else if (category != null && category.equals(cmd.getCategory())) {
                result.add(cmd);
            }
        }
        return result;
    }
    
    /**
     * Add command
     */
    public void addCommand(QuickCommand command) {
        commands.add(command);
        fireCommandsChanged();
    }
    
    /**
     * Remove command
     */
    public void removeCommand(QuickCommand command) {
        commands.remove(command);
        fireCommandsChanged();
    }
    
    /**
     * Update command
     */
    public void updateCommand(QuickCommand command) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getId().equals(command.getId())) {
                commands.set(i, command);
                fireCommandsChanged();
                break;
            }
        }
    }
    
    /**
     * Search commands
     */
    public List<QuickCommand> searchCommands(String keyword) {
        List<QuickCommand> result = new ArrayList<>();
        String lowerKey = keyword.toLowerCase();
        for (QuickCommand cmd : commands) {
            if (cmd.getName().toLowerCase().contains(lowerKey) ||
                cmd.getCommand().toLowerCase().contains(lowerKey) ||
                (cmd.getDescription() != null && cmd.getDescription().toLowerCase().contains(lowerKey))) {
                result.add(cmd);
            }
        }
        return result;
    }
    
    // Listeners
    public void addListener(QuickCommandListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(QuickCommandListener listener) {
        listeners.remove(listener);
    }
    
    private void fireCommandsChanged() {
        for (QuickCommandListener listener : listeners) {
            try {
                listener.onCommandsChanged();
            } catch (Exception e) {
                logger.error("Listener error", e);
            }
        }
    }
    
    /**
     * Quick command listener
     */
    public interface QuickCommandListener {
        void onCommandsChanged();
    }
}
