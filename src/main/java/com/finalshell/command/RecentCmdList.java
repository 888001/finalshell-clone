package com.finalshell.command;

import java.util.*;
import java.io.*;

/**
 * 最近命令列表
 * 管理最近使用的命令历史
 */
public class RecentCmdList {
    
    private static final int DEFAULT_MAX_SIZE = 100;
    private LinkedList<String> commands = new LinkedList<>();
    private int maxSize;
    private File saveFile;
    
    public RecentCmdList() {
        this(DEFAULT_MAX_SIZE);
    }
    
    public RecentCmdList(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public void add(String command) {
        if (command == null || command.trim().isEmpty()) return;
        commands.remove(command);
        commands.addFirst(command);
        while (commands.size() > maxSize) {
            commands.removeLast();
        }
    }
    
    public List<String> getCommands() {
        return new ArrayList<>(commands);
    }
    
    public List<String> search(String keyword) {
        List<String> result = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (String cmd : commands) {
            if (cmd.toLowerCase().contains(lower)) {
                result.add(cmd);
            }
        }
        return result;
    }
    
    public void clear() {
        commands.clear();
    }
    
    public int size() {
        return commands.size();
    }
    
    public void setSaveFile(File file) {
        this.saveFile = file;
    }
    
    public void save() throws IOException {
        if (saveFile == null) return;
        try (PrintWriter writer = new PrintWriter(new FileWriter(saveFile))) {
            for (String cmd : commands) {
                writer.println(cmd);
            }
        }
    }
    
    public void load() throws IOException {
        if (saveFile == null || !saveFile.exists()) return;
        commands.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    commands.add(line);
                }
            }
        }
    }
}
