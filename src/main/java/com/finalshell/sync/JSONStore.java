package com.finalshell.sync;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON存储工具类
 * 用于同步数据的JSON序列化存储
 */
public class JSONStore {
    
    private File storeFile;
    private Map<String, String> data = new HashMap<>();
    
    public JSONStore(String path) {
        this.storeFile = new File(path);
        load();
    }
    
    public JSONStore(File file) {
        this.storeFile = file;
        load();
    }
    
    public void put(String key, String value) {
        data.put(key, value);
    }
    
    public String get(String key) {
        return data.get(key);
    }
    
    public String get(String key, String defaultValue) {
        return data.getOrDefault(key, defaultValue);
    }
    
    public void remove(String key) {
        data.remove(key);
    }
    
    public boolean contains(String key) {
        return data.containsKey(key);
    }
    
    public void clear() {
        data.clear();
    }
    
    public void save() {
        try {
            if (!storeFile.getParentFile().exists()) {
                storeFile.getParentFile().mkdirs();
            }
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(storeFile), StandardCharsets.UTF_8))) {
                writer.write("{");
                boolean first = true;
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    if (!first) writer.write(",");
                    writer.write("\"" + escapeJson(entry.getKey()) + "\":\"" + escapeJson(entry.getValue()) + "\"");
                    first = false;
                }
                writer.write("}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void load() {
        if (!storeFile.exists()) return;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(storeFile), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            parseJson(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void parseJson(String json) {
        // 简单JSON解析
        data.clear();
    }
    
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
