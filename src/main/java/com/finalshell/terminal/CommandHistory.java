package com.finalshell.terminal;

import com.finalshell.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Command History - Records and retrieves command history
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CommandHistory {
    
    private static final Logger logger = LoggerFactory.getLogger(CommandHistory.class);
    private static final int MAX_HISTORY = 1000;
    
    private final String sessionId;
    private final LinkedList<HistoryEntry> history = new LinkedList<>();
    private int currentIndex = -1;
    
    public CommandHistory(String sessionId) {
        this.sessionId = sessionId;
        loadHistory();
    }
    
    /**
     * Add command to history
     */
    public void addCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        
        // Don't add duplicates consecutively
        if (!history.isEmpty() && history.getLast().getCommand().equals(command)) {
            return;
        }
        
        HistoryEntry entry = new HistoryEntry(command, System.currentTimeMillis());
        history.addLast(entry);
        
        // Limit history size
        while (history.size() > MAX_HISTORY) {
            history.removeFirst();
        }
        
        // Reset navigation index
        currentIndex = history.size();
    }
    
    /**
     * Get previous command in history
     */
    public String getPrevious() {
        if (history.isEmpty()) {
            return null;
        }
        
        if (currentIndex > 0) {
            currentIndex--;
        }
        
        return history.get(currentIndex).getCommand();
    }
    
    /**
     * Get next command in history
     */
    public String getNext() {
        if (history.isEmpty() || currentIndex >= history.size() - 1) {
            currentIndex = history.size();
            return "";
        }
        
        currentIndex++;
        return history.get(currentIndex).getCommand();
    }
    
    /**
     * Reset navigation index to end
     */
    public void resetIndex() {
        currentIndex = history.size();
    }
    
    /**
     * Get all history entries
     */
    public List<HistoryEntry> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * Get recent history
     */
    public List<HistoryEntry> getRecentHistory(int count) {
        List<HistoryEntry> result = new ArrayList<>();
        int start = Math.max(0, history.size() - count);
        for (int i = start; i < history.size(); i++) {
            result.add(history.get(i));
        }
        return result;
    }
    
    /**
     * Search history
     */
    public List<HistoryEntry> searchHistory(String keyword) {
        List<HistoryEntry> result = new ArrayList<>();
        String lowerKey = keyword.toLowerCase();
        for (HistoryEntry entry : history) {
            if (entry.getCommand().toLowerCase().contains(lowerKey)) {
                result.add(entry);
            }
        }
        return result;
    }
    
    /**
     * Clear history
     */
    public void clearHistory() {
        history.clear();
        currentIndex = -1;
    }
    
    /**
     * Load history from file
     */
    private void loadHistory() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File historyDir = new File(configDir, "history");
        File file = new File(historyDir, sessionId + ".dat");
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                List<HistoryEntry> loaded = (List<HistoryEntry>) ois.readObject();
                history.addAll(loaded);
                currentIndex = history.size();
                logger.debug("Loaded {} history entries for session {}", history.size(), sessionId);
            } catch (Exception e) {
                logger.warn("Failed to load history: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Save history to file
     */
    public void saveHistory() {
        File configDir = ConfigManager.getInstance().getConfigDir();
        File historyDir = new File(configDir, "history");
        historyDir.mkdirs();
        File file = new File(historyDir, sessionId + ".dat");
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(history));
            logger.debug("Saved {} history entries for session {}", history.size(), sessionId);
        } catch (Exception e) {
            logger.error("Failed to save history", e);
        }
    }
    
    /**
     * History Entry
     */
    public static class HistoryEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private final String command;
        private final long timestamp;
        
        public HistoryEntry(String command, long timestamp) {
            this.command = command;
            this.timestamp = timestamp;
        }
        
        public String getCommand() { return command; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return command;
        }
    }
}
