package com.finalshell.portforward;

import java.util.ArrayList;
import java.util.List;

/**
 * 端口转发配置
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class PFConfig {
    
    private String id;
    private String name;
    private List<PFRule> rules;
    private boolean autoStart;
    private String description;
    
    public PFConfig() {
        this.rules = new ArrayList<>();
    }
    
    public PFConfig(String id, String name) {
        this();
        this.id = id;
        this.name = name;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<PFRule> getRules() {
        return rules;
    }
    
    public void setRules(List<PFRule> rules) {
        this.rules = rules;
    }
    
    public void addRule(PFRule rule) {
        rules.add(rule);
    }
    
    public void removeRule(PFRule rule) {
        rules.remove(rule);
    }
    
    public boolean isAutoStart() {
        return autoStart;
    }
    
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getRuleCount() {
        return rules.size();
    }
    
    public PFRule getRuleById(String ruleId) {
        for (PFRule rule : rules) {
            if (rule.getId().equals(ruleId)) {
                return rule;
            }
        }
        return null;
    }
}
