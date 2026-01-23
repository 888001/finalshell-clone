package com.finalshell.forward;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * SSH加速映射规则管理器
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: SSHTunnel_UI_DeepAnalysis.md
 */
public class MapRuleManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MapRuleManager.class);
    
    private static MapRuleManager instance;
    
    private final Path rulesFile;
    private final List<MapRule> rules = new ArrayList<>();
    private final List<MapRuleListener> listeners = new ArrayList<>();
    
    private MapRuleManager() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        
        Path configDir;
        if (os.contains("win")) {
            configDir = Paths.get(System.getenv("APPDATA"), "finalshell");
        } else if (os.contains("mac")) {
            configDir = Paths.get(userHome, "Library/Application Support/finalshell");
        } else {
            configDir = Paths.get(userHome, ".finalshell");
        }
        
        this.rulesFile = configDir.resolve("maprules.json");
        
        try {
            Files.createDirectories(configDir);
        } catch (IOException e) {
            logger.error("Failed to create config directory", e);
        }
        
        loadRules();
    }
    
    public static synchronized MapRuleManager getInstance() {
        if (instance == null) {
            instance = new MapRuleManager();
        }
        return instance;
    }
    
    private void loadRules() {
        rules.clear();
        
        if (Files.exists(rulesFile)) {
            try {
                String json = new String(Files.readAllBytes(rulesFile), StandardCharsets.UTF_8);
                List<MapRule> loaded = JSON.parseArray(json, MapRule.class);
                if (loaded != null) {
                    rules.addAll(loaded);
                }
                logger.info("Loaded {} map rules", rules.size());
            } catch (IOException e) {
                logger.error("Failed to load map rules", e);
            }
        }
    }
    
    private void saveRules() {
        try {
            String json = JSON.toJSONString(rules, SerializerFeature.PrettyFormat);
            Files.write(rulesFile, json.getBytes(StandardCharsets.UTF_8));
            logger.info("Saved {} map rules", rules.size());
        } catch (IOException e) {
            logger.error("Failed to save map rules", e);
        }
    }
    
    public List<MapRule> getRules() {
        return new ArrayList<>(rules);
    }
    
    public MapRule getRuleById(String id) {
        for (MapRule rule : rules) {
            if (rule.getId().equals(id)) {
                return rule;
            }
        }
        return null;
    }
    
    public MapRule getRuleByName(String name) {
        for (MapRule rule : rules) {
            if (rule.getName().equals(name)) {
                return rule;
            }
        }
        return null;
    }
    
    public List<MapRule> getRulesByConnectConfig(String connectConfigId) {
        List<MapRule> result = new ArrayList<>();
        for (MapRule rule : rules) {
            if (connectConfigId.equals(rule.getConnectConfigId())) {
                result.add(rule);
            }
        }
        return result;
    }
    
    public void addRule(MapRule rule) {
        rules.add(rule);
        saveRules();
        fireRulesChanged();
    }
    
    public void removeRule(String id) {
        rules.removeIf(r -> r.getId().equals(id));
        saveRules();
        fireRulesChanged();
    }
    
    public void remove(String name) {
        rules.removeIf(r -> r.getName().equals(name));
        saveRules();
        fireRulesChanged();
    }
    
    public void updateRule(MapRule rule) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).getId().equals(rule.getId())) {
                rules.set(i, rule);
                break;
            }
        }
        saveRules();
        fireRulesChanged();
    }
    
    public boolean isLocalPortUsed(int port, String excludeId) {
        for (MapRule rule : rules) {
            if (rule.getLocalPort() == port && !rule.getId().equals(excludeId)) {
                return true;
            }
        }
        return false;
    }
    
    public void addListener(MapRuleListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(MapRuleListener listener) {
        listeners.remove(listener);
    }
    
    private void fireRulesChanged() {
        for (MapRuleListener listener : listeners) {
            listener.onRulesChanged();
        }
    }
    
    public interface MapRuleListener {
        void onRulesChanged();
    }
}
