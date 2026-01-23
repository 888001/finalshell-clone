package com.finalshell.command;

/**
 * 快捷命令
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCmd {
    
    private String id;
    private String name;
    private String command;
    private String groupId;
    private String description;
    private String hotkey;
    private int order;
    private boolean enabled;
    
    public QuickCmd() {
        this.id = java.util.UUID.randomUUID().toString();
        this.enabled = true;
    }
    
    public QuickCmd(String name, String command) {
        this();
        this.name = name;
        this.command = command;
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
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getHotkey() {
        return hotkey;
    }
    
    public void setHotkey(String hotkey) {
        this.hotkey = hotkey;
    }
    
    public int getOrder() {
        return order;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
