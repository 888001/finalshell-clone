package com.finalshell.monitor.parser;

import com.finalshell.monitor.TaskInfo;

import java.util.*;

/**
 * PS命令输出解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PSAllParser extends BaseParser {
    
    @Override
    public void parse() {
        // Parse using rawOutput from parent class
        // Results stored in TaskInfo list
    }
    
    public List<TaskInfo> parse(String output) {
        List<TaskInfo> list = new ArrayList<>();
        
        if (output == null || output.isEmpty()) {
            return list;
        }
        
        String[] lines = output.split("\n");
        boolean headerPassed = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            if (!headerPassed) {
                if (line.contains("PID") || line.contains("USER")) {
                    headerPassed = true;
                }
                continue;
            }
            
            TaskInfo task = parseLine(line);
            if (task != null) {
                list.add(task);
            }
        }
        
        return list;
    }
    
    private TaskInfo parseLine(String line) {
        String[] parts = line.split("\\s+", 11);
        if (parts.length < 11) {
            return null;
        }
        
        try {
            TaskInfo task = new TaskInfo();
            task.setUser(parts[0]);
            task.setPid(Integer.parseInt(parts[1]));
            task.setCpuPercent(Double.parseDouble(parts[2]));
            task.setMemPercent(Double.parseDouble(parts[3]));
            task.setVsz(Long.parseLong(parts[4]));
            task.setRss(Long.parseLong(parts[5]));
            task.setStat(parts[7]);
            task.setTime(parts[9]);
            task.setCommand(parts[10]);
            
            return task;
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<TaskInfo> parseTop(String output) {
        List<TaskInfo> list = new ArrayList<>();
        
        if (output == null || output.isEmpty()) {
            return list;
        }
        
        String[] lines = output.split("\n");
        boolean inProcessSection = false;
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            
            if (line.contains("PID") && line.contains("USER")) {
                inProcessSection = true;
                continue;
            }
            
            if (inProcessSection) {
                TaskInfo task = parseTopLine(line);
                if (task != null) {
                    list.add(task);
                }
            }
        }
        
        return list;
    }
    
    private TaskInfo parseTopLine(String line) {
        String[] parts = line.split("\\s+", 12);
        if (parts.length < 12) {
            return null;
        }
        
        try {
            TaskInfo task = new TaskInfo();
            task.setPid(Integer.parseInt(parts[0]));
            task.setUser(parts[1]);
            task.setPriority(Integer.parseInt(parts[2]));
            task.setNice(Integer.parseInt(parts[3]));
            task.setVsz(parseSize(parts[4]));
            task.setRss(parseSize(parts[5]));
            task.setStat(parts[7]);
            task.setCpuPercent(Double.parseDouble(parts[8]));
            task.setMemPercent(Double.parseDouble(parts[9]));
            task.setTime(parts[10]);
            task.setCommand(parts[11]);
            
            return task;
        } catch (Exception e) {
            return null;
        }
    }
    
    private long parseSize(String sizeStr) {
        try {
            sizeStr = sizeStr.toUpperCase().trim();
            long multiplier = 1;
            
            if (sizeStr.endsWith("K")) {
                multiplier = 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            } else if (sizeStr.endsWith("M")) {
                multiplier = 1024 * 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            } else if (sizeStr.endsWith("G")) {
                multiplier = 1024 * 1024 * 1024;
                sizeStr = sizeStr.substring(0, sizeStr.length() - 1);
            }
            
            return (long) (Double.parseDouble(sizeStr) * multiplier);
        } catch (Exception e) {
            return 0;
        }
    }
}
