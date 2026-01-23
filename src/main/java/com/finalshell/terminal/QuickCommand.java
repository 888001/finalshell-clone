package com.finalshell.terminal;

import java.util.UUID;

/**
 * Quick Command - Saved command for quick execution
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCommand {
    
    private String id;
    private String name;
    private String command;
    private String description;
    private String category;
    private String shortcut;
    private boolean sendEnter = true;
    private int order;
    
    public QuickCommand() {
        this.id = UUID.randomUUID().toString();
    }
    
    public QuickCommand(String name, String command) {
        this();
        this.name = name;
        this.command = command;
    }
    
    public QuickCommand(String name, String command, String description) {
        this(name, command);
        this.description = description;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getShortcut() { return shortcut; }
    public void setShortcut(String shortcut) { this.shortcut = shortcut; }
    
    public boolean isSendEnter() { return sendEnter; }
    public void setSendEnter(boolean sendEnter) { this.sendEnter = sendEnter; }
    
    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }
    
    /**
     * Get display string for UI
     */
    public String getDisplayString() {
        if (shortcut != null && !shortcut.isEmpty()) {
            return name + " (" + shortcut + ")";
        }
        return name;
    }
    
    @Override
    public String toString() {
        return name + ": " + command;
    }
}
