package com.finalshell.parser;

import java.util.*;

/**
 * /etc/*-release 系统信息解析器
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class CatEtcSysParser extends BaseParser {
    
    private Map<String, String> properties;
    
    public CatEtcSysParser() {
        this.properties = new HashMap<>();
    }
    
    @Override
    public void parse(String content) {
        if (content == null || content.isEmpty()) return;
        
        properties.clear();
        String[] lines = content.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            
            int equalsIndex = line.indexOf('=');
            if (equalsIndex > 0) {
                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();
                value = value.replace("\"", "").replace("'", "");
                properties.put(key, value);
            }
        }
    }
    
    public String getDistroName() {
        return properties.getOrDefault("NAME", 
               properties.getOrDefault("DISTRIB_ID", ""));
    }
    
    public String getVersion() {
        return properties.getOrDefault("VERSION_ID", 
               properties.getOrDefault("DISTRIB_RELEASE", ""));
    }
    
    public String getPrettyName() {
        return properties.getOrDefault("PRETTY_NAME", getDistroName() + " " + getVersion());
    }
    
    public String get(String key) {
        return properties.get(key);
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
}
