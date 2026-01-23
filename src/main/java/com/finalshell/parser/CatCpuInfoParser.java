package com.finalshell.parser;

import java.util.*;
import java.util.regex.*;

/**
 * /proc/cpuinfo 解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CatCpuInfoParser extends BaseParser {
    
    private List<Map<String, String>> cpuInfoList;
    
    public CatCpuInfoParser() {
        this.cpuInfoList = new ArrayList<>();
    }
    
    @Override
    public void parse(String content) {
        if (content == null || content.isEmpty()) return;
        
        Map<String, String> currentCpu = new HashMap<>();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                if (!currentCpu.isEmpty()) {
                    cpuInfoList.add(currentCpu);
                    currentCpu = new HashMap<>();
                }
                continue;
            }
            
            int colonIndex = line.indexOf(':');
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                currentCpu.put(key, value);
            }
        }
        
        if (!currentCpu.isEmpty()) {
            cpuInfoList.add(currentCpu);
        }
    }
    
    public int getCpuCount() {
        return cpuInfoList.size();
    }
    
    public String getModelName() {
        if (cpuInfoList.isEmpty()) return "";
        return cpuInfoList.get(0).getOrDefault("model name", "");
    }
    
    public String getVendorId() {
        if (cpuInfoList.isEmpty()) return "";
        return cpuInfoList.get(0).getOrDefault("vendor_id", "");
    }
    
    public String getCpuMHz() {
        if (cpuInfoList.isEmpty()) return "";
        return cpuInfoList.get(0).getOrDefault("cpu MHz", "");
    }
    
    public String getCacheSize() {
        if (cpuInfoList.isEmpty()) return "";
        return cpuInfoList.get(0).getOrDefault("cache size", "");
    }
    
    public int getCores() {
        if (cpuInfoList.isEmpty()) return 0;
        String cores = cpuInfoList.get(0).getOrDefault("cpu cores", "1");
        try {
            return Integer.parseInt(cores);
        } catch (NumberFormatException e) {
            return 1;
        }
    }
    
    public List<Map<String, String>> getCpuInfoList() {
        return cpuInfoList;
    }
}
